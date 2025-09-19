package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回收发票实体
 */
@Getter
@Setter
public class RecycleInvoice extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 发票号码
     */
    @TableField("invoice_no")
    private String invoiceNo;
    
    /**
     * 发票类型（进项、销项）
     */
    @TableField("invoice_type")
    private String invoiceType;
    
    /**
     * 开票银行
     */
    @TableField("invoice_bank")
    private String invoiceBank;
    
    /**
     * 计划开票时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("planned_invoice_time")
    private Date plannedInvoiceTime;
    
    /**
     * 状态
     */
    @TableField("status")
    private String status;
    
    /**
     * 经办人
     */
    @TableField("processor")
    private String processor;
    
    /**
     * 开票时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("invoice_time")
    private Date invoiceTime;
    
    /**
     * 总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;
    
    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal taxAmount;
    
    /**
     * 不含税金额
     */
    @TableField("amount_without_tax")
    private BigDecimal amountWithoutTax;
}