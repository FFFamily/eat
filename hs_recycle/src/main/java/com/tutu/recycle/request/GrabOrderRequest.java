package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 抢单请求
 */
@Getter
@Setter
public class GrabOrderRequest {
    
    /**
     * 主订单ID（UserOrder的ID）
     */
    private String orderId;
    
    /**
     * 经办人ID
     */
    private String processorId;
}

