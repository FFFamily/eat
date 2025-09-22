package com.tutu.recycle.request;

import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 发票详情响应DTO
 */
@Getter
@Setter
public class InvoiceDetailResponse {
    
    /**
     * 发票信息
     */
    private RecycleInvoice invoice;
    
    /**
     * 发票明细列表
     */
    private List<RecycleInvoiceDetail> details;
}
