package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.enums.RecycleMoneyDirectionEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.mapper.RecycleFundMapper;

import java.math.BigDecimal;

import jakarta.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecycleFundService extends ServiceImpl<RecycleFundMapper, RecycleFund> {
    @Resource
    private RecycleOrderService recycleOrderService;
    @Resource
    private RecycleCapitalPoolService recycleCapitalPoolService;
    /**
     * 新增走款记录
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(RecycleFund entity) {
        // 编号唯一
        String no = entity.getNo();
        RecycleFund one = this.getOne(new LambdaQueryWrapper<RecycleFund>().eq(RecycleFund::getNo, no));
        if (one != null) {
            throw new RuntimeException("编号已存在");
        }
        RecycleOrder order = recycleOrderService.getById(entity.getOrderId());
        if (order == null) {
            throw new RuntimeException("走款记录对应订单不存在：" + entity.getOrderId());
        }
        if (RecycleOrderTypeEnum.isPayOrderType(order.getType())) {
            entity.setFundFlowDirection(RecycleMoneyDirectionEnum.PAY.getCode());
            entity.setFundPoolDirection(RecycleMoneyDirectionEnum.PAY.getCode());
            entity.setFundDirection(RecycleMoneyDirectionEnum.PAY.getCode());
        }else{
            entity.setFundFlowDirection(RecycleMoneyDirectionEnum.OUT.getCode());
            entity.setFundPoolDirection(RecycleMoneyDirectionEnum.OUT.getCode());
            entity.setFundDirection(RecycleMoneyDirectionEnum.OUT.getCode());
        }
        this.save(entity);
    }

    /**
     * 批量新增走款记录
     * @param entityList
     */
    @Transactional(rollbackFor = Exception.class)
    public void createBatch(List<RecycleFund> entityList) {
        for (RecycleFund entity : entityList) {
            this.create(entity);
        }
    }

        /**
     * 确认走款
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirm(RecycleFund entity) {
        // 更新走款记录状态
        this.updateById(entity);
        
        // 使用安全的金额变更方法更新资金池余额
        BigDecimal amount = entity.getFundAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("走款金额不能为空或零");
        }
        
        // 根据走款方向决定是增加还是减少资金池余额
        if (RecycleMoneyDirectionEnum.PAY.getCode().equals(entity.getFundPoolDirection())) {
            // 付款操作，减少资金池余额
            recycleCapitalPoolService.decreaseBalance(
                entity.getContractNo(), 
                amount, 
                "确认走款-付款", 
                entity.getOrderId().toString()
            );
        } else {
            // 收款操作，增加资金池余额
            recycleCapitalPoolService.increaseBalance(
                entity.getContractNo(), 
                amount, 
                "确认走款-收款", 
                entity.getOrderId().toString()
            );
        }
    }

} 