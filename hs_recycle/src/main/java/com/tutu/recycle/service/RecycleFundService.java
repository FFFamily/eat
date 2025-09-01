package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.enums.RecycleMoneyDirectionEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.mapper.RecycleFundMapper;

import jakarta.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecycleFundService extends ServiceImpl<RecycleFundMapper, RecycleFund> {
    @Resource
    private RecycleOrderService recycleOrderService;

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

} 