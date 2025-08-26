package com.tutu.invoice.dto;

import com.tutu.invoice.enums.InvoiceHeadType;
import lombok.Data;

/**
 * 发票抬头数据传输对象
 */
@Data
public class InvoiceHeadDTO {
    
    private String id;
    
    private String accountId;
    
    private InvoiceHeadType type;
    
    private String title;
    
    // 企业必填，个人可不填
    private String taxNumber;
    
    // 企业必填，个人可不填
    private String registeredAddress;
    
    private String phone;
    
    // 企业必填，个人可不填
    private String bankName;
    
    // 企业必填，个人可不填
    private String bankCode;
    
    // 企业必填，个人可不填
    private String bankAccount;
    
    private String email;
    
    private Boolean isDefault;
} 