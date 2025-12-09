package com.tutu.recycle.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;


@Getter
@Setter
public class UserOrder extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    // 订单编号
    private String no;
    //订单状态 阶段
    private String stage;
    // 合同id
    private String contractId;
    // 合同编号
    private String contractNo;
    // 合同名称
    private String contractName;
    // 合同合作方
    private String contractPartner;
    // 合同合作方名称
    private String contractPartnerName;
    // 甲方
    private String partyA;
    // 甲方名称
    private String partyAName;
    // 乙方
    private String partyB;
    // 乙方名称
    private String partyBName;
    // 客户签名
    private String partnerSignature;
    // 经办人签名
    private String processorSignature;
    // 计价方式
    private String pricingMethod;
    // 运输方式（上门回收/送货上门）
    private String transportMethod;
    // 用户评级系数
    private BigDecimal accountCoefficient;
    // 其他调价
    private BigDecimal otherAdjustAmount;
    // 订单总金额
    private BigDecimal totalAmount;
    // 货物总金额
    private BigDecimal goodsTotalAmount;
    // 交付方式
    private String deliveryMethod;
    // 交付照片
    private String deliveryPhoto;
    // 交付状态
    private String deliveryStatus;
    // 结算状态(已结算/待确认/未结算/已驳回/超时自动结算)
    private String settlementStatus;
    // 交付备注
    private String deliveryRemark;
    // 交付重量
    private BigDecimal deliveryWeight;
    // 结算时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date settlementTime;
    // 交付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;
    // 结算确认时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date confirmTime;
    // 结算确认备注
    private String confirmRemark;
    // 结算单PDF URL
    @TableField("settlement_pdf")
    private String settlementPdf;
    // 交付单PDF URL
    @TableField("delivery_pdf")
    private String deliveryPdf;
    // 申请单PDF URL
    @TableField("application_pdf")
    private String applicationPdf;
}
