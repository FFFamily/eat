package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 运输订单提交请求
 */
@Getter
@Setter
public class TransportOrderSubmitRequest {
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单图片
     */
    private String orderNodeImg;
    
    /**
     * 交付地址
     */
    private String deliveryAddress;
}