package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 交付状态枚举
 */
@Getter
@AllArgsConstructor
public enum DeliveryStatusEnum {

    /**
     * 未交付
     */
    NOT_DELIVERED("not_delivered", "未交付"),

    /**
     * 已交付
     */
    DELIVERED("delivered", "已交付"),

    /**
     * 无需交付
     */
    NO_NEED("no_need", "无需交付");

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
    public static DeliveryStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (DeliveryStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 验证状态代码是否有效
     * @param code 状态代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
