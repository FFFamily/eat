package com.tutu.lease.enums;

import lombok.Getter;

/**
 * 租赁订单状态枚举
 */
@Getter
public enum LeaseOrderStatusEnum {
    
    /**
     * 待审核
     */
    PENDING_REVIEW("PENDING_REVIEW", "待审核"),
    
    /**
     * 租赁中
     */
    LEASING("LEASING", "租赁中"),
    
    /**
     * 待开票
     */
    PENDING_INVOICE("PENDING_INVOICE", "待开票"),
    
    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成");
    
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
