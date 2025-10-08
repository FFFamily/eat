package com.tutu.system.enums;

import lombok.Getter;
import lombok.AllArgsConstructor;
@Getter
@AllArgsConstructor 
public enum MessageTypeEnum {
    /**
     * 订单结算消息
     */
    ORDER_SETTLE("order_settle","您的订单已结算"),
    /**
     * 货款结算消息
     */
    FUND_SETTLE("fund_settle","您的货款已结算");
    private final String type;
    private final String title;
}
