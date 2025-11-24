package com.tutu.recycle.request;

import com.tutu.recycle.entity.order.RecycleOrderItem;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 加工订单提交请求
 */
@Getter
@Setter
public class ProcessingOrderSubmitRequest {
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 当前经办人ID
     */
    private String processorId;
    /**
     * 订单明细列表
     */
    private List<RecycleOrderItem> items;
}