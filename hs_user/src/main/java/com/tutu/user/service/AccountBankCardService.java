package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.AccountBankCard;
import com.tutu.user.mapper.AccountBankCardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户银行卡信息 Service 接口
 */
@Service
public class AccountBankCardService extends ServiceImpl<AccountBankCardMapper,AccountBankCard> {

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
     * 校验默认银行卡规则
     * 一个用户只能有一个默认银行卡
     */
    private void validateDefaultCard(AccountBankCard entity) {
        // 只有当设置为默认卡时才需要校验
        if ("1".equals(entity.getIsDefault())) {
            LambdaQueryWrapper<AccountBankCard> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AccountBankCard::getAccountId, entity.getAccountId())
                       .eq(AccountBankCard::getIsDefault, "1");
            
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