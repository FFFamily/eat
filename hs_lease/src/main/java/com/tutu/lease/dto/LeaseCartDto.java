package com.tutu.lease.dto;

import com.tutu.lease.entity.LeaseCart;
import com.tutu.lease.entity.LeaseGood;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物车详情DTO
 */
@Getter
@Setter
public class LeaseCartDto {
    
    /**
     * 购物车ID
     */
    private String id;
    
    /**
     * 用户ID
     */
    private String userId;
    
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
    private Date leaseStartTime;
    
    /**
     * 租赁结束时间
     */
    private Date leaseEndTime;
    
    /**
     * 租赁天数
     */
    private Integer leaseDays;
    
    /**
     * 小计金额
     */
    private BigDecimal subtotal;
    
    /**
     * 购物车状态
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 商品详细信息
     */
    private LeaseGood leaseGood;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
