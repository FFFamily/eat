package com.tutu.recycle.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.mapper.RecycleOrderMapper;

import cn.hutool.core.util.IdUtil;

import org.springframework.stereotype.Service;

@Service
public class RecycleOrderService extends ServiceImpl<RecycleOrderMapper, RecycleOrder> {
    /**
     * 创建回收订单
     * @param recycleOrder 回收订单信息
     */
    public void createOrder(RecycleOrder recycleOrder) {
        // 生成订单编号
        recycleOrder.setOrderNo(IdUtil.simpleUUID());
        // 状态
        recycleOrder.setStatus(RecycleOrderStatusEnum.PENDING_PICKUP.getCode());
        // 保存订单
        save(recycleOrder);
    }
}
