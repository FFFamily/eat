package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 运输订单查询请求
 */
@Getter
@Setter
public class TransportOrderListRequest {

    /**
     * 经办人ID
     */
    private String processorId;
}

