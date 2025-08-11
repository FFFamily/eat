package com.tutu.lease.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租赁订单主表
 */
@Getter
@Setter
@TableName("lease_order")
public class LeaseOrder extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;
    /**
     * 订单状态
     */
    private String status;
    // 租赁开始时间
    private Date leaseStartTime;
    // 租赁结束时间
    private Date leaseEndTime;
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 最后成交价
     */
    private BigDecimal paidAmount;

    /**
     * 押金金额
     */
    private BigDecimal depositAmount;
   
    /**
     * 完成时间
     */
    private Date completeTime;

    /**
     * 订单备注
     */
    private String remark;
}
