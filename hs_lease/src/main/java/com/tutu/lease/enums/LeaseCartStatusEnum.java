package com.tutu.lease.enums;

import lombok.Getter;

@Getter
public enum LeaseCartStatusEnum {
    NORMAL(0, "正常"),
    ORDERED(1, "已下单"),
    DELETED(2, "已删除");

    private final int code;
    private final String description;

    LeaseCartStatusEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static LeaseCartStatusEnum fromCode(Integer code) {
        if (code == null) return null;
        for (LeaseCartStatusEnum v : values()) {
            if (v.code == code) return v;
        }
        return null;
    }
}