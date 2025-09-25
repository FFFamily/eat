package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 发票列表查询请求
 */
@Getter
@Setter
public class InvoiceListRequest {
    /**
     * 发票状态过滤（可选）
     */
    private String status;
    
    /**
     * 发票类型过滤（可选）
     */
    private String invoiceType;
}
