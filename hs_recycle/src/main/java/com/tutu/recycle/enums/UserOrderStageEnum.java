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
     * 加工阶段 -> 待分拣
     */
    PROCESSING("processing", "加工","wait_warehousing"),
    
    /**
     * 入库阶段 -> 待结算
     */
    WAREHOUSING("warehousing", "入库","wait_settlement"),
    /**
     * 待结算阶段 -> 已完成
     */
    PENDING_SETTLEMENT("pending_settlement", "待结算","completed"),
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
        switch (this) {
            case PURCHASE:
                return TRANSPORT;
            case TRANSPORT:
                return PROCESSING;
            case PROCESSING:
                return WAREHOUSING;
            case WAREHOUSING:
                return PENDING_SETTLEMENT;
            case PENDING_SETTLEMENT:
                return COMPLETED;
            case COMPLETED:
                return null; // 已经是最后一个阶段
            default:
                return null;
        }
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

        switch (this) {
            case PURCHASE:
                return method.isNeedTransportStage() ? TRANSPORT : PROCESSING;
            case TRANSPORT:
                return PROCESSING;
            case PROCESSING:
                return WAREHOUSING;
            case WAREHOUSING:
                return PENDING_SETTLEMENT;
            case PENDING_SETTLEMENT:
                return COMPLETED;
            case COMPLETED:
                return null; // 已经是最后一个阶段
            default:
                return null;
        }
    }
    
    /**
     * 获取上一个阶段
     * @return 上一个阶段，如果是第一个阶段则返回null
     */
    public UserOrderStageEnum getPreviousStage() {
        switch (this) {
            case PURCHASE:
                return null; // 已经是第一个阶段
            case TRANSPORT:
                return PURCHASE;
            case PROCESSING:
                return TRANSPORT;
            case WAREHOUSING:
                return PROCESSING;
            case PENDING_SETTLEMENT:
                return WAREHOUSING;
            case COMPLETED:
                return PENDING_SETTLEMENT;
            default:
                return null;
        }
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

