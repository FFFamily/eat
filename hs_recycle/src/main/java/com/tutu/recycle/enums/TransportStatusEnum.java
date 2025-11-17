package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 运输状态枚举
 */
@Getter
@AllArgsConstructor
public enum TransportStatusEnum {

    /**
     * 可接单（抢单大厅）
     */
    AVAILABLE("available", "可接单"),

    /**
     * 交付大厅（已抢单，待交付）
     */
    GRABBED("grabbed", "交付大厅"),

    /**
     * 运输中（已交付，运输中）
     */
    TRANSPORTING("transporting", "运输中"),

    /**
     * 已送达
     */
    ARRIVED("arrived", "已送达");

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
    public static TransportStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (TransportStatusEnum status : values()) {
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

    /**
     * 获取下一个状态
     * @return 下一个状态，如果是最后一个状态则返回null
     */
    public TransportStatusEnum getNextStatus() {
        switch (this) {
            case AVAILABLE:
                return GRABBED;
            case GRABBED:
                return TRANSPORTING;
            case TRANSPORTING:
                return ARRIVED;
            case ARRIVED:
                return null; // 已经是最后一个状态
            default:
                return null;
        }
    }

    /**
     * 判断是否可以流转到目标状态
     * @param target 目标状态
     * @return 是否可以流转
     */
    public boolean canTransitionTo(TransportStatusEnum target) {
        if (target == null) {
            return false;
        }
        // 只能流转到下一个状态
        return this.getNextStatus() == target;
    }
}
