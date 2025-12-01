package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.AccountCustomer;
import com.tutu.user.mapper.AccountCustomerMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 账户客户关系 Service
 */
@Service
public class AccountCustomerService extends ServiceImpl<AccountCustomerMapper, AccountCustomer> {

    /**
     * 根据账户ID查询客户列表（带账户名称）
     */
    public List<AccountCustomer> listByAccountIdWithAccount(String accountId) {
        return baseMapper.selectByAccountIdWithAccount(accountId);
    }

    /**
     * 根据客户账户ID查询账户列表（带账户名称）
     */
    public List<AccountCustomer> listByCustomerAccountIdWithAccount(String customerAccountId) {
        return baseMapper.selectByCustomerAccountIdWithAccount(customerAccountId);
    }

    /**
     * 根据账户ID查询客户列表
     */
    public List<AccountCustomer> listByAccountId(String accountId) {
        LambdaQueryWrapper<AccountCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountCustomer::getAccountId, accountId);
        return list(queryWrapper);
    }

    /**
     * 根据客户账户ID查询账户列表
     */
    public List<AccountCustomer> listByCustomerAccountId(String customerAccountId) {
        LambdaQueryWrapper<AccountCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountCustomer::getCustomerAccountId, customerAccountId);
        return list(queryWrapper);
    }

    /**
     * 根据ID查询详情（带账户名称）
     */
    public AccountCustomer getByIdWithAccount(String id) {
        return baseMapper.selectByIdWithAccount(id);
    }

    /**
     * 分页查询（带账户名称）
     */
    public IPage<AccountCustomer> pageWithAccount(Page<AccountCustomer> page, AccountCustomer query) {
        return baseMapper.selectPageWithAccount(page, query);
    }

    /**
     * 保存账户客户关系（新增或更新）
     * 校验：同一账户不能重复添加同一个客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AccountCustomer entity) {
        validateDuplicate(entity);
        return super.save(entity);
    }

    /**
     * 更新账户客户关系
     * 校验：同一账户不能重复添加同一个客户
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AccountCustomer entity) {
        validateDuplicate(entity);
        return super.updateById(entity);
    }

    /**
     * 校验重复关系
     * 同一账户不能重复添加同一个客户
     */
    private void validateDuplicate(AccountCustomer entity) {
        if (entity.getAccountId() == null || entity.getCustomerAccountId() == null) {
            throw new ServiceException("账户ID和客户账户ID不能为空");
        }
        
        // 不能自己添加自己为客户
        if (entity.getAccountId().equals(entity.getCustomerAccountId())) {
            throw new ServiceException("不能将自己添加为客户");
        }

        // 使用自定义方法检查是否存在未删除的重复记录
        long count = baseMapper.countActiveDuplicate(
                entity.getAccountId(), 
                entity.getCustomerAccountId(),
                entity.getId() // 更新时排除当前记录
        );
        
        if (count > 0) {
            throw new ServiceException("该客户已存在，不能重复添加");
        }
    }

    /**
     * 根据账户ID和客户账户ID删除关系
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByAccountIdAndCustomerAccountId(String accountId, String customerAccountId) {
        LambdaQueryWrapper<AccountCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountCustomer::getAccountId, accountId)
                   .eq(AccountCustomer::getCustomerAccountId, customerAccountId);
        return remove(queryWrapper);
    }

    /**
     * 根据账户ID删除所有客户关系
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByAccountId(String accountId) {
        LambdaQueryWrapper<AccountCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountCustomer::getAccountId, accountId);
        remove(queryWrapper);
    }

    /**
     * 根据客户账户ID删除所有关系
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCustomerAccountId(String customerAccountId) {
        LambdaQueryWrapper<AccountCustomer> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountCustomer::getCustomerAccountId, customerAccountId);
        remove(queryWrapper);
    }
}

