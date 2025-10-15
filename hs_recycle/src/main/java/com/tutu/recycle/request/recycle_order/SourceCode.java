package com.tutu.recycle.request.recycle_order;

import lombok.Data;

/**
 * 来源识别码
 */
@Data
public class SourceCode {
    // 源识别码
    private String identifyCode;
    // 订单ID
    private String orderId;
    // 订单编号
    private String orderNo;
    // 变更原因
    private String changeReason;
    // 订单类型
    private String orderType;
}
