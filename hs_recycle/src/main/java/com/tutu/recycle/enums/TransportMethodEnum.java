package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运输方式枚举
 */
@Getter
@AllArgsConstructor
public enum TransportMethodEnum {
    /**
     * 上门回收：需要运输阶段
     */
    DOOR_TO_DOOR_RECYCLE("door_to_door_recycle", "上门回收", true),

    /**
     * 送货上门：无需运输阶段
     */
    HOME_DELIVERY("home_delivery", "送货上门", false);

    private final String code;
    private final String description;
    private final boolean needTransportStage;

    public static TransportMethodEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (TransportMethodEnum method : values()) {
            if (method.getCode().equals(code)) {
                return method;
            }
        }
        return null;
    }

    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}


