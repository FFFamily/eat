package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 保存交付单PDF请求
 */
@Getter
@Setter
public class SaveDeliveryNotePdfRequest {
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 交付单PDF URL
     */
    private String pdfUrl;
}

