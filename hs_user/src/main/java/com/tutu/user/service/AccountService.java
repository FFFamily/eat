package com.tutu.user.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.common.util.PasswordUtil;
import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.Account;
import com.tutu.user.entity.AccountType;
import com.tutu.user.enums.UserUseTypeEnum;
import com.tutu.user.mapper.AccountMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class AccountService extends ServiceImpl<AccountMapper, Account> {

    @Resource
    private AccountTypeService accountTypeService;


    /**
     * 获取对应账户的用户
     */
    public Account getUserByUsername(String username) {
        if (StrUtil.isBlank(username)) {
            throw new ServiceException("用户名不能为空");
        }
        QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return getOne(queryWrapper);
    }

    /**
     * 获取所有的useType
     * @return
     */
    public List<Map<String,String>> getAllUseType() {
        return Arrays.stream(UserUseTypeEnum.values()).map(item -> {
            Map<String,String> map = new HashMap<>();
            map.put("key", item.getCode());
            map.put("value", item.getTitle());
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 根据用户类型生成对应的用户账号
     * @param accountTypeId 用户类型
     * @return 用户账号
     */
    public String generateAccountUsername(String accountTypeId) {
        // 查询当前用户类型对应的用户数量
        long count = this.baseMapper.getUserCountByAccountTypeId(accountTypeId);
        // 转为String
        StringBuilder countStr = new StringBuilder(String.valueOf(count + 1));
        // 根据长度构建编码：00001
        int length = countStr.length();
        while (length < 5) {
            countStr.insert(0, "0");
            length++;
        }   
        // 查询accountTypeId对应的名称
        AccountType accountType = accountTypeService.getById(accountTypeId);
        if (accountType == null) {
            throw new ServiceException("用户类型accountType不存在");
        }
        // 生成用户账号
        return accountType.getCode() + countStr;
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
            throw new RuntimeException("用户名已被使用");
        }
        oldAccount = getUserByIdCard(account.getIdCard());
        if (oldAccount != null) {
            throw new RuntimeException("身份证号已被使用");
        }
        oldAccount = getUserByPhone(account.getPhone());
        if (oldAccount != null) {
            throw new RuntimeException("手机号已被使用");
        }
        account.setPassword(PasswordUtil.encode(account.getPassword()));
        account.setStatus(UserStatusEnum.USE.getCode());
        save(account);
    }

    /**
     * 根据身份证号获取用户
     * @param idCard 身份证号
     * @return 用户实体，若不存在则返回 null
     */
    private Account getUserByIdCard(String idCard) {
        if (StrUtil.isBlank(idCard)) {
            return null;
        }
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
        if (StrUtil.isBlank(phone)) {
            return null;
        }
        LambdaQueryWrapper<Account> userLambdaQueryWrapper = new LambdaQueryWrapper<>();
        userLambdaQueryWrapper.eq(Account::getPhone, phone);
        return getOne(userLambdaQueryWrapper);
    }

    /**
     * 更新用户
     * @param account
     */
    public void updateUser(Account account) {
        if (account == null) {
            throw new RuntimeException("用户不存在");
        }
        // 加密密码
        account.setPassword(PasswordUtil.encode(account.getPassword()));
        updateById(account);
    }
}
