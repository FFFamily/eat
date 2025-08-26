package com.tutu.invoice.enums;

/**
 * 发票抬头类型枚举
 */
public enum InvoiceHeadType {
    PERSONAL("个人"),
    ENTERPRISE("企业");

    private final String description;

    InvoiceHeadType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 