package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 出库类型枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryOutTypeEnum {
    
    /**
     * 销售出库
     */
    SALE("sale", "销售出库"),
    
    /**
     * 报损出库
     */
    LOSS("loss", "报损出库"),
    
    /**
     * 调拨出库
     */
    TRANSFER("transfer", "调拨出库"),
    
    /**
     * 其他出库
     */
    OTHER("other", "其他出库");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static InventoryOutTypeEnum getByCode(String code) {
        for (InventoryOutTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

