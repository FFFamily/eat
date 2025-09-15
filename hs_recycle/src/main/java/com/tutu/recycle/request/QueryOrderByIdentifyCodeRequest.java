package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 根据识别号查询订单请求
 */
@Getter
@Setter
public class QueryOrderByIdentifyCodeRequest {
    
    /**
     * 订单识别号
     */
    private String identifyCode;
}