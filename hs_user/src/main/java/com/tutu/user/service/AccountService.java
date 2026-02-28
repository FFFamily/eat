package com.tutu.user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.tutu.common.util.PasswordUtil;
import com.tutu.user.enums.AccountBusinessTypeEnum;
import jakarta.annotation.Resource;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.Account;
import com.tutu.user.entity.AccountType;
import com.tutu.user.enums.UserUseTypeEnum;
import com.tutu.user.mapper.AccountMapper;
import com.tutu.user.mapper.AccountUsernameSeqMapper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService extends ServiceImpl<AccountMapper, Account> {

    @Resource
    private AccountTypeService accountTypeService;
    @Resource
    private AccountUsernameSeqMapper accountUsernameSeqMapper;


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
    @Transactional(rollbackFor = Exception.class)
    public String generateAccountUsername(String accountId,String accountTypeId) {
        if (StrUtil.isBlank(accountTypeId)) {
            throw new ServiceException("用户类型accountTypeId不能为空");
        }
        if (!StrUtil.isBlank(accountId)) {
            // 老用户需要判断是否又是这个用户类型
            Account account = getById(accountId);
            if (account == null) {
                throw new ServiceException("用户不存在");
            }
            if (accountTypeId.equals(account.getAccountTypeId())) {
                return account.getUsername();
            }
        }
        // 使用数据库序列生成编号（按类型自增）
        long seq = nextUsernameSeq(accountTypeId);
        // 转为String
        StringBuilder countStr = new StringBuilder(String.valueOf(seq));
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
        if (account == null) {
            throw new ServiceException("用户不存在");
        }
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
            throw new ServiceException("用户不存在");
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
            throw new ServiceException("用户名已被使用");
        }
        oldAccount = getUserByPhone(account.getPhone());
        if (oldAccount != null) {
            throw new ServiceException("手机号已被使用");
        }
        account.setPassword(PasswordUtil.encode(account.getPassword()));
        account.setStatus(UserStatusEnum.USE.getCode());
        account.setBusinessType(AccountBusinessTypeEnum.SUPPLIER.getCode());
        save(account);
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
            throw new ServiceException("用户不存在");
        }
        Account exist = getById(account.getId());
        if (exist == null) {
            throw new ServiceException("用户不存在");
        }
        // 加密密码（仅在显式传入时更新）
        if (StrUtil.isNotBlank(account.getPassword())) {
            account.setPassword(PasswordUtil.encode(account.getPassword()));
        } else {
            account.setPassword(null);
        }
        BeanUtil.copyProperties(account, exist, CopyOptions.create().setIgnoreNullValue(true));
        updateById(exist);
    }

    /**
     * 变更账户业务类型
     * @param accountRequest 用户实体
     */
    public void changeAccountBusinessType(Account accountRequest) {
        Account account = getById(accountRequest.getId());
        if (account == null) {
            throw new ServiceException("用户不存在");
        }
        account.setBusinessType(accountRequest.getBusinessType());
        updateById(account);
    }

    /**
     * 查询业务类型为服务商的账户列表
     * @return 服务商账户列表
     */
    public List<Account> getServiceProviderList() {
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getBusinessType, AccountBusinessTypeEnum.SERVICE_PROVIDER.getCode());
        return list(queryWrapper);
    }

    public void updatePasswordById(String id, String encodedPassword) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(encodedPassword)) {
            return;
        }
        LambdaUpdateWrapper<Account> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Account::getId, id)
                .set(Account::getPassword, encodedPassword)
                .set(Account::getUpdateTime, new Date());
        update(updateWrapper);
    }

    private long nextUsernameSeq(String accountTypeId) {
        accountUsernameSeqMapper.upsertAndIncrement(accountTypeId);
        Long next = accountUsernameSeqMapper.selectLastInsertId();
        if (next == null || next <= 0) {
            throw new ServiceException("生成账号编号失败");
        }
        return next;
    }
}
