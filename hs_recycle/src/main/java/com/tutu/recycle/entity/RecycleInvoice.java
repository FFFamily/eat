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
    // 主键ID
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    // 发票号码
    private String invoiceNo;
    // 发票类型（进项、销项）
    private String invoiceType;
    // 开票银行
    private String invoiceBank;
    // 计划开票时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date plannedInvoiceTime;
    // 状态
    private String status;
    // 经办人
    private String processor;
    // 开票时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date invoiceTime;
    // 总金额
    private BigDecimal totalAmount;
    // 税额
    private BigDecimal taxAmount;
    // 不含税金额
    private BigDecimal amountWithoutTax;
}