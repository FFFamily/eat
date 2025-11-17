package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 交付订单请求
 */
@Getter
@Setter
public class DeliverOrderRequest {
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 送达地址（可选）
     */
    private String deliveryAddress;
}

