package com.tutu.recycle.enums;

public enum RecycleMoneyDirectionEnum {
    OUT("0", "收款"),
    PAY("1", "付款");
    ;
    private final String code;
    private final String name;

    RecycleMoneyDirectionEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }
    
}
