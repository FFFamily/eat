package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 校验订单识别码请求
 */
@Getter
@Setter
public class ValidateOrderIdentifyCodeRequest {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 订单识别码
     */
    private String identifyCode;
}

