package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.AccountBankCard;
import com.tutu.user.mapper.AccountBankCardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户银行卡信息 Service 接口
 */
@Service
public class AccountBankCardService extends ServiceImpl<AccountBankCardMapper,AccountBankCard> {

    /**
     * 分页查询银行卡信息（带账户名称）
     */
    public Page<AccountBankCard> pageWithAccount(Page<AccountBankCard> page, AccountBankCard query) {
        return baseMapper.selectPageWithAccount(page, query);
    }

    /**
     * 根据ID查询银行卡详情（带账户名称）
     */
    public AccountBankCard getByIdWithAccount(String id) {
        return baseMapper.selectByIdWithAccount(id);
    }

    /**
     * 根据账户ID查询银行卡列表（带账户名称）
     */
    public List<AccountBankCard> listByAccountIdWithAccount(String accountId) {
        return baseMapper.selectByAccountIdWithAccount(accountId);
    }

    /**
     * 保存银行卡信息（新增或更新）
     * 校验：一个用户只能有一个默认银行卡
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean save(AccountBankCard entity) {
        validateDefaultCard(entity);
        return super.save(entity);
    }

    /**
     * 更新银行卡信息
     * 校验：一个用户只能有一个默认银行卡
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateById(AccountBankCard entity) {
        validateDefaultCard(entity);
        return super.updateById(entity);
    }

    /**
     * 更新默认银行卡
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateDefaultCard(String id) {
        // 查询这条记录
        AccountBankCard bankCard = getById(id);
        // 将该用户对应的所有银行卡设置为非默认
        LambdaQueryWrapper<AccountBankCard> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AccountBankCard::getAccountId, bankCard.getAccountId());
        List<AccountBankCard> accountCards = list(queryWrapper);
        accountCards.forEach(item -> item.setIsDefault(CommonConstant.NO_STR));
        updateBatchById(accountCards);
        // 将当前银行卡设置为默认
        bankCard.setIsDefault(CommonConstant.YES_STR);
        updateById(bankCard);
    }

    /**
     * 校验默认银行卡规则
     * 一个用户只能有一个默认银行卡
     */
    private void validateDefaultCard(AccountBankCard entity) {
        // 只有当设置为默认卡时才需要校验
        if (CommonConstant.YES_STR.equals(entity.getIsDefault())) {
            LambdaQueryWrapper<AccountBankCard> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountBankCard::getAccountId, entity.getAccountId())
                       .eq(AccountBankCard::getIsDefault, CommonConstant.YES_STR);
            // 如果是更新操作，需要排除当前银行卡
            if (entity.getId() != null) {
                queryWrapper.ne(AccountBankCard::getId, entity.getId());
            }
            long count = this.count(queryWrapper);
            if (count > 0) {
                throw new ServiceException("该用户已存在默认银行卡，无法再设置默认");
            }
        }
    }
}