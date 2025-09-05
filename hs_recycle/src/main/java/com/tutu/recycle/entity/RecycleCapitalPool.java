package com.tutu.recycle.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;
/**
 * 资金池
 */
@Getter
@Setter
public class RecycleCapitalPool extends BaseEntity {
    // 资金池ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 编号
    private String no;
    // 资金池余额
    private BigDecimal balance;
    // 合同id
    private String contractId;
    // 合同编号
    @TableField(exist = false)
    private String contractNo;
    // 合同名称
    @TableField(exist = false)
    private String contractName;
    // 合作方
    private String contractPartner;
    // 合作方名称
    @TableField(exist = false)
    private String contractPartnerName;
    // 资金池方向（收款/付款）
    private String fundPoolDirection;
    // 初始金额
    private BigDecimal initialAmount;
    // 初始余额凭证
    private String initialBalanceVoucher;
}
