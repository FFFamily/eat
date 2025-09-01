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
 * 资金流水走款
 */
@Getter
@Setter
public class RecycleFund extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 走款编号
     */
    private String no;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 合同名称
     */
    private String contractName;
    @TableField(exist = false)
    private Date contractStartTime;
    @TableField(exist = false)
    private Date contractEndTime;

    /**
     * 合作方
     */
    private String partner;

    /**
     * 订单ID
     */
    private Long orderId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单类型
     */
    private String orderType;
    /**
     * 订单总金额
     */
    private BigDecimal orderTotalAmount;

    /**
     * 订单应走金额
     */
    private BigDecimal orderShouldAmount;

    /**
     * 合同资金池方向
     */
    private String contractFundPoolDirection;

    /**
     * 合同资金池剩余金额
     */
    private BigDecimal contractFundPoolBalance;

    /**
     * 走款方向
     */
    private String fundFlowDirection;

    /**
     * 走款金额
     */
    private BigDecimal fundFlowAmount;

    /**
     * 资金池方向（+进/-出）
     */
    private String fundPoolDirection;

    /**
     * 资金池走款金额
     */
    private BigDecimal fundPoolAmount;

    // 货款方向
    private String fundDirection;

    // 货款走款金额
    private BigDecimal fundAmount;

    // 货款走款银行
    private String fundBank;

    /**
     * 计划走款时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planPayTime;

    /**
     * 经办人
     */
    private String processor;

    /**
     * 凭证
     */
    private String voucher;

    /**
     * 支付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date payFundTime;

    /**
     * 走款状态（0-待确认，1-已确认，2-已取消）
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
