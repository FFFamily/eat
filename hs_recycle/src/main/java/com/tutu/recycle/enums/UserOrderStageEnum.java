package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户订单阶段枚举
 */
@Getter
@AllArgsConstructor
public enum UserOrderStageEnum {
    
    /**
     * 采购阶段 -> 待运输
     */
    PURCHASE("purchase", "采购","wait_transport"),
    
    /**
     * 运输阶段 -> 待分拣
     */
    TRANSPORT("transport", "运输","wait_sorting"),
    
    /**
     * 入库阶段 -> 待结算
     */
    PROCESSING("processing", "入库","wait_settlement"),
    /**
     * 待结算阶段 -> 待客户确认
     */
    PENDING_SETTLEMENT("pending_settlement", "待结算","wait_customer_confirm"),
    /**
     * 待客户确认阶段 -> 已完成
     */
    PENDING_CUSTOMER_CONFIRMATION("pending_customer_confirmation", "待客户确认","completed"),
    /**
     * 已完成 -> 无
     */
    COMPLETED("completed", "已完成",null);
    
    /**
     * 阶段代码
     */
    private final String code;
    /**
     * 阶段描述
     */
    private final String description;
        /**
     * 下一个状态
     */
    private final String nextStatus;
    
    /**
     * 根据代码获取枚举
     * @param code 阶段代码
     * @return 对应的枚举值，如果不存在返回null
     */
    public static UserOrderStageEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (UserOrderStageEnum stage : values()) {
            if (stage.getCode().equals(code)) {
                return stage;
            }
        }
        return null;
    }
    
    /**
     * 验证阶段代码是否有效
     * @param code 阶段代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
    
    /**
     * 获取下一个阶段
     * @return 下一个阶段，如果是最后一个阶段则返回null
     */
    public UserOrderStageEnum getNextStage() {
        return switch (this) {
            case PURCHASE -> TRANSPORT;
            case TRANSPORT -> PROCESSING;
            case PROCESSING -> PENDING_SETTLEMENT;
            case PENDING_SETTLEMENT -> PENDING_CUSTOMER_CONFIRMATION;
            case PENDING_CUSTOMER_CONFIRMATION -> COMPLETED;
            case COMPLETED -> null; // 已经是最后一个阶段
            default -> null;
        };
    }

    /**
     * 根据计价方式获取下一个阶段
     * @param pricingMethod 计价方式
     * @return 下一个阶段，如果是最后一个阶段则返回null
     */
    public UserOrderStageEnum getNextStage(String transportMethod) {
        // 如果运输方式为空，使用默认流程
        if (transportMethod == null) {
            return getNextStage();
        }

        TransportMethodEnum method = TransportMethodEnum.getByCode(transportMethod);

        if (method == null) {
            return getNextStage();
        }

        return switch (this) {
            case PURCHASE -> method.isNeedTransportStage() ? TRANSPORT : PROCESSING;
            case TRANSPORT -> PROCESSING;
            case PROCESSING -> PENDING_SETTLEMENT;
            case PENDING_SETTLEMENT -> PENDING_CUSTOMER_CONFIRMATION;
            case PENDING_CUSTOMER_CONFIRMATION -> COMPLETED;
            case COMPLETED -> null; // 已经是最后一个阶段
        };
    }
    
    /**
     * 获取上一个阶段
     * @return 上一个阶段，如果是第一个阶段则返回null
     */
    public UserOrderStageEnum getPreviousStage() {
        return switch (this) {
            case PURCHASE -> null; // 已经是第一个阶段
            case TRANSPORT -> PURCHASE;
            case PROCESSING -> TRANSPORT;
            case PENDING_SETTLEMENT -> PROCESSING;
            case PENDING_CUSTOMER_CONFIRMATION -> PENDING_SETTLEMENT;
            case COMPLETED -> PENDING_CUSTOMER_CONFIRMATION;
        };
    }
    
    /**
     * 判断是否是第一个阶段
     * @return 是否是第一个阶段
     */
    public boolean isFirstStage() {
        return this == PURCHASE;
    }
    
    /**
     * 判断是否是最后一个阶段
     * @return 是否是最后一个阶段
     */
    public boolean isLastStage() {
        return this == COMPLETED;
    }
}

