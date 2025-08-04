package com.tutu.lease.enums;

import lombok.Getter;

/**
 * 租赁订单状态枚举
 */
@Getter
public enum LeaseOrderStatusEnum {
    
    /**
     * 待支付
     */
    PENDING_PAYMENT("PENDING_PAYMENT", "待支付"),
    
    /**
     * 已支付，待发货
     */
    PAID("PAID", "已支付"),
    
    /**
     * 已发货，待收货
     */
    SHIPPED("SHIPPED", "已发货"),
    
    /**
     * 已收货，租赁中
     */
    LEASING("LEASING", "租赁中"),
    
    /**
     * 租赁到期，待归还
     */
    LEASE_EXPIRED("LEASE_EXPIRED", "租赁到期"),
    
    /**
     * 已归还，待确认
     */
    RETURNED("RETURNED", "已归还"),
    
    /**
     * 订单完成
     */
    COMPLETED("COMPLETED", "已完成"),
    
    /**
     * 订单取消
     */
    CANCELLED("CANCELLED", "已取消"),
    
    /**
     * 退款中
     */
    REFUNDING("REFUNDING", "退款中"),
    
    /**
     * 已退款
     */
    REFUNDED("REFUNDED", "已退款");
    
    private final String code;
    private final String description;
    
    LeaseOrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    /**
     * 根据code获取枚举
     */
    public static LeaseOrderStatusEnum getByCode(String code) {
        for (LeaseOrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
