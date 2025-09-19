package com.tutu.recycle.request;

import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.entity.RecycleInvoiceDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 更新发票请求对象
 */
@Getter
@Setter
public class UpdateInvoiceRequest {
    
    /**
     * 发票信息
     */
    private RecycleInvoice invoice;
    
    /**
     * 发票明细列表
     */
    private List<RecycleInvoiceDetail> details;
}