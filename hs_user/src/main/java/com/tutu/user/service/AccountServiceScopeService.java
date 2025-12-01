package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.AccountServiceScope;
import com.tutu.user.mapper.AccountServiceScopeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 更新账户的服务范围（省、市、区）
     * 先删除该账户的所有服务范围，然后批量保存新的服务范围
     * @param accountId 账户ID
     * @param serviceScopeList 服务范围列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateServiceScope(String accountId, List<AccountServiceScope> serviceScopeList) {
        // 删除该账户的所有服务范围
        deleteByAccountId(accountId);
        
        // 如果有新的服务范围，批量保存
        if (serviceScopeList != null && !serviceScopeList.isEmpty()) {
            // 确保每个服务范围都设置了 accountId
            for (AccountServiceScope scope : serviceScopeList) {
                scope.setId(null);
                scope.setAccountId(accountId);
            }
            saveBatch(serviceScopeList);
        }
    }
}

