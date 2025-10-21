package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 入库类型枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryInTypeEnum {
    
    /**
     * 采购入库
     */
    PURCHASE("purchase", "采购入库"),
    
    /**
     * 退货入库
     */
    RETURN("return", "退货入库"),
    
    /**
     * 调拨入库
     */
    TRANSFER("transfer", "调拨入库"),
    
    /**
     * 其他入库
     */
    OTHER("other", "其他入库");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static InventoryInTypeEnum getByCode(String code) {
        for (InventoryInTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

