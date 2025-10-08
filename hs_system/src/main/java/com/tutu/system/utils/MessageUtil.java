package com.tutu.system.utils;

import com.tutu.system.entity.Message;
import com.tutu.system.enums.MessageOriginEnum;
import com.tutu.system.enums.MessageTypeEnum;

public class MessageUtil {
    // 构建订单结算消息
    public static Message buildOrderSettleMessage(String userId,String orderId) {
        Message message = new Message();
        message.setType(MessageTypeEnum.ORDER_SETTLE.getType());
        message.setUserId(userId);
        message.setTitle(MessageTypeEnum.ORDER_SETTLE.getTitle());
        message.setContent(orderId);
        message.setOrigin(MessageOriginEnum.USER.getCode());
        return message;
    }
    // 构建货款结算消息
    public static Message buildFundSettleMessage(String userId,String recycleOrderFundId) {
        Message message = new Message();
        message.setType(MessageTypeEnum.FUND_SETTLE.getType());
        message.setUserId(userId);
        message.setTitle(MessageTypeEnum.FUND_SETTLE.getTitle());
        message.setContent(recycleOrderFundId);
        message.setOrigin(MessageOriginEnum.USER.getCode());
        return message;
    }
}
