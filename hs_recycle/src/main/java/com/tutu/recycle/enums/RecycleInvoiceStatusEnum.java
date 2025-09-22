package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 回收发票状态枚举
 */
@Getter
@AllArgsConstructor
public enum RecycleInvoiceStatusEnum {
    
    /**
     * 待开票
     */
    PENDING("pending", "待开票"),
    
    /**
     * 已开票
     */
    INVOICED("invoiced", "已开票");
    
    /**
     * 状态代码
     */
    private final String code;
    
    /**
     * 状态描述
     */
    private final String description;
    
    /**
     * 根据代码获取枚举
     * @param code 状态代码
     * @return 对应的枚举值，如果不存在返回null
     */
    public static RecycleInvoiceStatusEnum getByCode(String code) {
        for (RecycleInvoiceStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
