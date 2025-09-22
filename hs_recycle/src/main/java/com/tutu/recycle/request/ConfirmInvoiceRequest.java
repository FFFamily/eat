package com.tutu.recycle.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 确认发票请求DTO
 */
@Getter
@Setter
public class ConfirmInvoiceRequest {
    
    /**
     * 发票ID
     */
    private String invoiceId;
    
    /**
     * 经办人
     */
    private String processor;
    
    /**
     * 发票编号
     */
    private String invoiceNo;
    
    /**
     * 开票时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date invoiceTime;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 税额
     */
    private BigDecimal taxAmount;
}
