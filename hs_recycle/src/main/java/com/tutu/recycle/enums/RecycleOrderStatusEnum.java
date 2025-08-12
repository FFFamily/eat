package com.tutu.recycle.enums;

import lombok.Getter;

/**
 * 回收订单状态枚举
 */
@Getter
public enum RecycleOrderStatusEnum {

    /**
     * 待审核
     */
    PENDING("PENDING", "待审核"),

    /**
     * 已审核
     */
    APPROVED("APPROVED", "已审核"),

    /**
     * 运输中
     */
    TRANSPORTING("TRANSPORTING", "运输中"),

    /**
     * 已取货
     */
    PICKED_UP("PICKED_UP", "已取货"),

    /**
     * 分拣中
     */
    SORTING("SORTING", "分拣中"),

    /**
     * 分拣完成
     */
    SORTED("SORTED", "分拣完成"),

    /**
     * 待打款
     */
    PENDING_PAYMENT("PENDING_PAYMENT", "待打款"),

    /**
     * 已打款
     */
    PAID("PAID", "已打款"),

    /**
     * 待开票
     */
    PENDING_INVOICE("PENDING_INVOICE", "待开票"),

    /**
     * 已完成
     */
    COMPLETED("COMPLETED", "已完成"),

    /**
     * 已取消
     */
    CANCELLED("CANCELLED", "已取消");

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
