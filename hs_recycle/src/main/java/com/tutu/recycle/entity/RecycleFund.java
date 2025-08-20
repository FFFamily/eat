package com.tutu.recycle.entity;

import java.math.BigDecimal;
import java.sql.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;
/**
 * 资金流水
 */
@Getter
@Setter
public class RecycleFund extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 编号
    private String no;
    // 合同编号
    private String contractNo;
    // 合同类型
    private String contractType;
    // 合同名称
    private String contractName;
    // 合作方
    private String partner;
    // 订单ID
    private String orderId;
    // 订单编号
    private String orderNo;
    // 应走金额
    private BigDecimal shouldPay;
    // 本次交易金额
    private BigDecimal thisPay;
    // 资金池Id
    private String fundPoolId;
    // 本次交易资金池方向（进+/出-）
    private String fundPoolDirection;
    // 经办人
    private String processor;
    // 凭证
    private String voucher;
    // 支付时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date payFundTime;
}
