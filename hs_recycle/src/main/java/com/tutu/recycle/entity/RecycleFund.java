package com.tutu.recycle.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;
/**
 * 走款
 */
@Getter
@Setter
public class RecycleFund extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    // 合作方
    private String partner;
    // 合作方名称
    private String partnerName;
    // 合同ID
    private String contractId;
    // 合同编号
    @TableField(exist = false)
    private String contractNo;
    // 合同名称
    @TableField(exist = false)
    private String contractName;
    // 合同开始时间
    @TableField(exist = false)
    private Date contractStartTime;
    // 合同结束时间
    @TableField(exist = false)
    private Date contractEndTime;
    // 订单ID
    private Long orderId;
    // 订单编号
    @TableField(exist = false)
    private String orderNo;
    // 订单类型
    @TableField(exist = false)
    private String orderType;
    // 订单总金额
    private BigDecimal orderTotalAmount;
    // 订单应走金额
    private BigDecimal orderShouldAmount;
    // 走款方向
    private String fundFlowDirection;
    // 走款金额
    private BigDecimal fundFlowAmount;
    // 资金池方向（+进/-出）
    private String fundPoolDirection;
    // 资金池走款金额
    private BigDecimal fundPoolAmount;
    // 货款方向
    private String fundDirection;
    // 货款走款金额
    private BigDecimal fundAmount;
    // 货款走款银行
    private String fundBank;
    // 计划走款时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planPayTime;
    // 经办人
    private String processor;
    // 凭证
    private String voucher;
    // 支付时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payFundTime;
    // 走款状态（0-待确认，1-已确认，2-已取消）
    private Integer status;
}
