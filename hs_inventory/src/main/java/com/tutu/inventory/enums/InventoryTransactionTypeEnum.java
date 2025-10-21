package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存交易类型枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryTransactionTypeEnum {
    
    /**
     * 入库
     */
    IN("in", "入库"),
    
    /**
     * 出库
     */
    OUT("out", "出库");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static InventoryTransactionTypeEnum getByCode(String code) {
        for (InventoryTransactionTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

