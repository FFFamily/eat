package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 发票详情查询请求
 */
@Getter
@Setter
public class InvoiceDetailRequest {
    /**
     * 发票ID
     */
    private String invoiceId;
}
