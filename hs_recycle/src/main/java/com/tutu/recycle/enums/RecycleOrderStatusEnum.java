package com.tutu.recycle.enums;

import lombok.Getter;

/**
 * 回收订单状态枚举
 */
@Getter
public enum RecycleOrderStatusEnum {

    /**
     * 执行中
     */
    PROCESSING("processing", "执行中"),

    /**
     * 已结算
     */
    COMPLETED("completed", "已结算");

    private final String code;
    private final String description;

    RecycleOrderStatusEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据code获取枚举
     */
    public static RecycleOrderStatusEnum getByCode(String code) {
        for (RecycleOrderStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
