package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 根据ID查询订单请求
 */
@Getter
@Setter
public class QueryOrderByIdRequest {
    
    /**
     * 订单ID
     */
    private String orderId;
    // 确认备注
    private String confirmRemark;
}