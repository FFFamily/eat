package com.tutu.inventory.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 业务类型枚举
 */
@Getter
@AllArgsConstructor
public enum InventoryBusinessTypeEnum {
    
    /**
     * 采购
     */
    PURCHASE("purchase", "采购"),
    
    /**
     * 销售
     */
    SALE("sale", "销售"),
    
    /**
     * 调拨
     */
    TRANSFER("transfer", "调拨"),
    
    /**
     * 报损
     */
    LOSS("loss", "报损"),
    
    /**
     * 退货
     */
    RETURN("return", "退货"),
    
    /**
     * 其他
     */
    OTHER("other", "其他");
    
    private final String code;
    private final String description;
    
    /**
     * 根据code获取枚举
     */
    public static InventoryBusinessTypeEnum getByCode(String code) {
        for (InventoryBusinessTypeEnum type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}

