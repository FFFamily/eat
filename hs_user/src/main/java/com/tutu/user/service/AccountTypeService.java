package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.AccountType;
import com.tutu.user.mapper.AccountTypeMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 账号类型Service
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AccountTypeService extends ServiceImpl<AccountTypeMapper, AccountType> {

    /**
     * 分页查询账号类型
     * @param page 分页对象
     * @param accountType 查询条件
     * @return 分页结果
     */
    public IPage<AccountType> getPage(Page<AccountType> page, AccountType accountType) {
        LambdaQueryWrapper<AccountType> queryWrapper = new LambdaQueryWrapper<>();
        
        if (accountType != null) {
            // 根据编号查询
            if (StringUtils.hasText(accountType.getCode())) {
                queryWrapper.like(AccountType::getCode, accountType.getCode());
            }
            
            // 根据类型名称查询
            if (StringUtils.hasText(accountType.getTypeName())) {
                queryWrapper.like(AccountType::getTypeName, accountType.getTypeName());
            }
        }
        
        // 按排序号和创建时间排序
        queryWrapper.orderByDesc(AccountType::getCreateTime);
        
        return page(page, queryWrapper);
    }

    /**
     * 获取所有启用的账号类型
     * @return 启用的账号类型列表
     */
    public List<AccountType> getEnabledTypes() {
        LambdaQueryWrapper<AccountType> queryWrapper = new LambdaQueryWrapper<>();
        return list(queryWrapper);
    }

    /**
     * 根据编号查询账号类型
     * @param code 编号
     * @return 账号类型
     */
    public AccountType getByCode(String code) {
        if (!StringUtils.hasText(code)) {
            return null;
        }
        LambdaQueryWrapper<AccountType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountType::getCode, code);
        return getOne(queryWrapper);
    }

    /**
     * 检查编号是否已存在
     * @param code 编号
     * @param excludeId 排除的ID（用于更新时检查）
     * @return 是否存在
     */
    public boolean isCodeExists(String code, String excludeId) {
        if (!StringUtils.hasText(code)) {
            return false;
        }
        LambdaQueryWrapper<AccountType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountType::getCode, code);
        if (StringUtils.hasText(excludeId)) {
            queryWrapper.ne(AccountType::getId, excludeId);
        }
        return count(queryWrapper) > 0;
    }
} 