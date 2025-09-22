package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 回收发票明细实体
 */
@Getter
@Setter
public class RecycleInvoiceDetail extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 发票ID
     */
    @TableField("invoice_id")
    private String invoiceId;
    
    /**
     * 订单编号
     */
    @TableField("order_id")
    private String orderId;
    
    /**
     * 订单总金额
     */
    @TableField("order_total_amount")
    private BigDecimal orderTotalAmount;
    
    /**
     * 订单实开发票
     */
    @TableField("order_actual_invoice")
    private BigDecimal orderActualInvoice;
    
    /**
     * 订单应开发票
     */
    @TableField("order_should_invoice")
    private BigDecimal orderShouldInvoice;
    // 订单编号
    @TableField(exist = false)
    private String orderNo;
    // 合作方
    @TableField(exist = false)
    private String orderPartner;
    // 订单合作方
    @TableField(exist = false)
    private String orderPartnerName;
    // 订单类型
    @TableField(exist = false)
    private String orderType;
}