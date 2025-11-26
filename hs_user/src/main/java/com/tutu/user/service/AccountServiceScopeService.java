package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.AccountServiceScope;
import com.tutu.user.mapper.AccountServiceScopeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 账户服务范围 Service
 */
@Service
public class AccountServiceScopeService extends ServiceImpl<AccountServiceScopeMapper, AccountServiceScope> {

    /**
     * 根据账户ID查询服务范围列表（带账户名称）
     */
    public List<AccountServiceScope> listByAccountIdWithAccount(String accountId) {
        return baseMapper.selectByAccountIdWithAccount(accountId);
    }

    /**
     * 根据账户ID查询服务范围列表
     */
    public List<AccountServiceScope> listByAccountId(String accountId) {
        LambdaQueryWrapper<AccountServiceScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountServiceScope::getAccountId, accountId);
        return list(queryWrapper);
    }

    /**
     * 根据账户ID删除所有服务范围
     */
    public void deleteByAccountId(String accountId) {
        LambdaQueryWrapper<AccountServiceScope> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountServiceScope::getAccountId, accountId);
        remove(queryWrapper);
    }
}

