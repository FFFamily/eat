package com.tutu.user.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.user.entity.Account;
import com.tutu.user.mapper.AccountMapper;

import org.springframework.stereotype.Service;

@Service
public class AccountService extends ServiceImpl<AccountMapper, Account> {


    /**
     * 获取对应账户的用户
     */
    public Account getUserByUsername(String username) {
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }



    /**
     * 修改用户使用类型
     * @param userId 用户 ID
     * @param useType 使用类型
     */
    public void changeUseType(String userId,String useType){
        Account account = getById(userId);
        account.setUseType(useType);
        updateById(account);
    }

    /**
     * 修改用户状态
     * @param userId 用户 ID
     */
    public void changeStatus(String userId) {
        Account account = getById(userId);
        if (account == null) {
            throw new RuntimeException("用户不存在");
        }
        account.setStatus(account.getStatus().equals(UserStatusEnum.USE.getCode()) ? UserStatusEnum.DISABLE.getCode() : UserStatusEnum.USE.getCode());
        updateById(account);
    }

    /**
     * 创建用户
     * @param account 用户实体
     */
    public void create(Account account) {
        Account oldAccount = getUserByUsername(account.getUsername());
        if (oldAccount != null) {
            throw new RuntimeException("用户名已存在");
        }
        oldAccount = getUserByIdCard(account.getIdCard());
        if (oldAccount != null) {
            throw new RuntimeException("身份证已存在");
        }
        oldAccount = getUserByPhone(account.getPhone());
        if (oldAccount != null) {
            throw new RuntimeException("手机号已存在");
        }
        account.setStatus(UserStatusEnum.USE.getCode());
        account.setPassword(SaSecureUtil.md5(account.getPassword()));
        save(account);
    }

    /**
     * 根据身份证号获取用户
     * @param idCard 身份证号
     * @return 用户实体，若不存在则返回 null
     */
    private Account getUserByIdCard(String idCard) {
        LambdaQueryWrapper<Account> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(Account::getIdCard, idCard);
        return getOne(userLambdaQueryWrapper);
    }

    /**
     * 根据手机号获取用户
     * @param phone 手机号
     * @return 用户实体，若不存在则返回 null
     */
    private Account getUserByPhone(String phone) {
        LambdaQueryWrapper<Account> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(Account::getPhone, phone);
        return getOne(userLambdaQueryWrapper);
    }
}
