package com.tutu.recycle.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 回收合同
 */
@Getter
@Setter
public class RecycleContract extends BaseEntity{
    // 合同ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 合同名称
    private String name;
    // 合同类型
    private String type;
    // 合作方
    private String partner;
    // 起始时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startTime;
    // 结束时间
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endTime;
    // 主银行卡号
    private String mainBankCard;
    // 开票信息
    private String invoiceInfo;
    // 走款节点
    private String payNode;
    // 开票节点
    private String invoiceNode;
    // 合同总金额
    private BigDecimal totalAmount;
    // 合同资金池
    private String pool;
}
