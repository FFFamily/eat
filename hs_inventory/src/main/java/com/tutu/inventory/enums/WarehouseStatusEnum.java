package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库状态枚举
 */
@Getter
@AllArgsConstructor
public enum WarehouseStatusEnum {
    
    /**
     * 启用
     */
    ACTIVE("active", "启用"),
    
    /**
     * 停用
     */
    INACTIVE("inactive", "停用");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static WarehouseStatusEnum getByCode(String code) {
        for (WarehouseStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}

