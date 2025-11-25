package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 结算状态枚举
 */
@Getter
@AllArgsConstructor
public enum SettlementStatusEnum {

    /**
     * 未结算
     */
    NOT_SETTLED("not_settled", "未结算"),
    /**
     * 待客户确认
     */
    WAITING_CONFIRMATION("waiting_confirmation", "待客户确认"),
    /**
     * 已结算
     */
    SETTLED("settled", "已结算"),

    /**
     * 已驳回
     */
    REJECTED("rejected", "已驳回"),

    /**
     * 超时自动结算
     */
    AUTO_SETTLED("auto_settled", "超时自动结算");

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
    public static SettlementStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (SettlementStatusEnum status : values()) {
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

