package com.tutu.lease.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 租赁订单明细表
 */
@Getter
@Setter
@TableName("lease_order_item")
public class LeaseOrderItem extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 商品ID
     */
    private String goodId;
    
    /**
     * 商品名称
     */
    private String goodName;
    
    /**
     * 商品单价
     */
    private BigDecimal goodPrice;
    
    /**
     * 租赁数量
     */
    private Integer quantity;
    
    /**
     * 租赁开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaseStartTime;
    
    /**
     * 租赁结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date leaseEndTime;
    
    /**
     * 租赁天数
     */
    private Integer leaseDays;
    
    /**
     * 小计金额（数量 * 单价 * 天数）
     */
    private BigDecimal subtotal;

    /**
     * 备注
     */
    private String remark;
}
