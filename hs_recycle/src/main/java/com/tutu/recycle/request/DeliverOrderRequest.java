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
     * 订单ID（前端可能直接传 id）
     */
    private String orderId;

    /**
     * 冗余字段，兼容前端直接传递 id
     */
    private String id;

    /**
     * 送达地址（可选）
     */
    private String deliveryAddress;

    /**
     * 客户签名
     */
    private String customerSignature;

    /**
     * 司机签名
     */
    private String processorSignature;
}

