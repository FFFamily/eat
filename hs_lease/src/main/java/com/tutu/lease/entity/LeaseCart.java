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
 * 租赁商品购物车
 */
@Getter
@Setter
@TableName("lease_cart")
public class LeaseCart extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
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
     * 商品名称（冗余字段，提高查询效率）
     */
    private String goodName;
    
    /**
     * 商品单价（冗余字段，防止商品价格变动影响购物车）
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
     * 小计金额（数量 * 单价 * 天数）
     */
    private BigDecimal subtotal;
    
    /**
     * 购物车状态：0-正常，1-已下单，2-已删除
     */
    private Integer status;
    
    /**
     * 备注
     */
    private String remark;
}
