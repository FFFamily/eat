package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 仓库类型枚举
 */
@Getter
@AllArgsConstructor
public enum WarehouseTypeEnum {
    
    /**
     * 原料仓
     */
    RAW_MATERIAL("raw_material", "原料仓"),
    
    /**
     * 成品仓
     */
    FINISHED_PRODUCT("finished_product", "成品仓"),
    
    /**
     * 中转仓
     */
    TRANSIT("transit", "中转仓");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static WarehouseTypeEnum getByCode(String code) {
        for (WarehouseTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

