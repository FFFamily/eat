package com.tutu.user.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.user.entity.User;
import com.tutu.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService extends ServiceImpl<UserMapper, User> {


    /**
     * 获取对应账户的用户
     */
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * 修改用户状态
     * @param id 用户 ID
     */
    public void changeStatus(String id) {
        User user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        user.setStatus(user.getStatus().equals(UserStatusEnum.USE.getCode()) ? UserStatusEnum.DISABLE.getCode() : UserStatusEnum.USE.getCode());
        updateById(user);
    }

    /**
     * 创建用户
     * @param user 用户实体
     */
    public void create(User user) {
        User oldUser = getUserByUsername(user.getUsername());
        if (oldUser != null) {
            throw new RuntimeException("用户名已存在");
        }
        oldUser = getUserByIdCard(user.getIdCard());
        if (oldUser != null) {
            throw new RuntimeException("身份证已存在");
        }
        oldUser = getUserByPhone(user.getPhone());
        if (oldUser != null) {
            throw new RuntimeException("手机号已存在");
        }
        user.setStatus(UserStatusEnum.USE.getCode());
        user.setPassword(SaSecureUtil.md5(user.getPassword()));
        save(user);
    }

    /**
     * 根据身份证号获取用户
     * @param idCard 身份证号
     * @return 用户实体，若不存在则返回 null
     */
    private User getUserByIdCard(String idCard) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getIdCard, idCard);
        return getOne(userLambdaQueryWrapper);
    }

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户实体，若不存在则返回 null
     */
    private User getUserByPhone(String phone) {
        LambdaQueryWrapper<User> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(User::getPhone, phone);
        return getOne(userLambdaQueryWrapper);
    }
}
