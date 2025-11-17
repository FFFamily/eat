package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 计价方式枚举
 */
@Getter
@AllArgsConstructor
public enum PricingMethodEnum {

    /**
     * 简单计价 - 没有运输阶段
     */
    SIMPLE("simple", "简单计价"),

    /**
     * 一般计价 - 有运输阶段
     */
    GENERAL("general", "一般计价");

    /**
     * 计价方式代码
     */
    private final String code;

    /**
     * 计价方式描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     * @param code 计价方式代码
     * @return 对应的枚举值，如果不存在返回null
     */
    public static PricingMethodEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (PricingMethodEnum method : values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 验证计价方式代码是否有效
     * @param code 计价方式代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }

    /**
     * 判断是否需要运输阶段
     * @return true表示需要运输阶段，false表示不需要
     */
    public boolean needTransportStage() {
        return this == GENERAL;
    }
}
