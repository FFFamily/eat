package com.tutu.recycle.dto;

import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.schema.RecycleOrderInfo;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户订单信息DTO
 * 包含用户订单和对应的回收订单信息
 * 根据回收订单类型，将订单信息映射到不同的字段
 */
@Getter
@Setter
public class UserOrderInfo {
    /**
     * 用户订单信息
     */
    private UserOrder userOrder;
    
    /**
     * 采购阶段订单信息
     */
    private RecycleOrderInfo purchaseOrder;
    
    /**
     * 运输阶段订单信息
     */
    private RecycleOrderInfo transportOrder;
    
    /**
     * 加工阶段订单信息
     */
    private RecycleOrderInfo processingOrder;
    
    /**
     * 仓储阶段订单信息
     */
    private RecycleOrderInfo storageOrder;
}
