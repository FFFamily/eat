package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 单据状态枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryStatusEnum {
    
    /**
     * 待处理
     */
    PENDING("pending", "待处理"),
    
    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),
    
    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static InventoryStatusEnum getByCode(String code) {
        for (InventoryStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

