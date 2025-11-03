package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户订单状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserOrderStatusEnum {
    
    /**
     * 待运输
     */
    WAIT_TRANSPORT("wait_transport", "待运输"),
    
    /**
     * 待分拣
     */
    WAIT_SORTING("wait_sorting", "待分拣"),
    
    /**
     * 待入库
     */
    WAIT_WAREHOUSING("wait_warehousing", "待入库"),
    
    /**
     * 已完成
     */
    COMPLETED("completed", "已完成");
    
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
    public static UserOrderStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserOrderStatusEnum status : values()) {
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
    public UserOrderStatusEnum getNextStatus() {
        switch (this) {
            case WAIT_TRANSPORT:
                return WAIT_SORTING;
            case WAIT_SORTING:
                return WAIT_WAREHOUSING;
            case WAIT_WAREHOUSING:
                return COMPLETED;
            case COMPLETED:
                return null; // 已经是最后一个状态
            default:
                return null;
        }
    }
    
    /**
     * 获取上一个状态
     * @return 上一个状态，如果是第一个状态则返回null
     */
    public UserOrderStatusEnum getPreviousStatus() {
        switch (this) {
            case WAIT_TRANSPORT:
                return null; // 已经是第一个状态
            case WAIT_SORTING:
                return WAIT_TRANSPORT;
            case WAIT_WAREHOUSING:
                return WAIT_SORTING;
            case COMPLETED:
                return WAIT_WAREHOUSING;
            default:
                return null;
        }
    }
    
    /**
     * 判断是否是完成状态
     * @return 是否已完成
     */
    public boolean isCompleted() {
        return this == COMPLETED;
    }
    
    /**
     * 判断是否是待处理状态（非完成状态）
     * @return 是否待处理
     */
    public boolean isPending() {
        return this != COMPLETED;
    }
    
    /**
     * 判断是否是第一个状态
     * @return 是否是第一个状态
     */
    public boolean isFirstStatus() {
        return this == WAIT_TRANSPORT;
    }
    
    /**
     * 判断是否是最后一个状态
     * @return 是否是最后一个状态
     */
    public boolean isLastStatus() {
        return this == COMPLETED;
    }
    
    /**
     * 判断是否可以流转到目标状态
     * @param targetStatus 目标状态
     * @return 是否可以流转
     */
    public boolean canTransitionTo(UserOrderStatusEnum targetStatus) {
        if (targetStatus == null) {
            return false;
        }
        // 已完成状态不能再流转
        if (this.isCompleted()) {
            return false;
        }
        // 只能流转到下一个状态或保持当前状态
        return targetStatus == this || targetStatus == this.getNextStatus();
    }
}

