package com.tutu.lease.dto;

import com.tutu.lease.entity.LeaseOrder;
import com.tutu.lease.entity.LeaseOrderItem;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 租赁订单详情DTO
 */
@Getter
@Setter
public class LeaseOrderDto {
    
    /**
     * 订单ID
     */
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
     * 用户手机号
     */
    private String userPhone;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 订单状态描述
     */
    private String statusDesc;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal paidAmount;
    
    /**
     * 押金金额
     */
    private BigDecimal depositAmount;
    
    /**
     * 租赁开始时间
     */
    private Date leaseStartTime;
    
    /**
     * 租赁结束时间
     */
    private Date leaseEndTime;
    
    /**
     * 租赁总天数
     */
    private Integer totalLeaseDays;
    
    /**
     * 收货人姓名
     */
    private String receiverName;
    
    /**
     * 收货人手机号
     */
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    private String receiverAddress;
    
    /**
     * 归还地址
     */
    private String returnAddress;
    
    /**
     * 支付时间
     */
    private Date payTime;
    
    /**
     * 发货时间
     */
    private Date shipTime;
    
    /**
     * 收货时间
     */
    private Date receiveTime;
    
    /**
     * 归还时间
     */
    private Date returnTime;
    
    /**
     * 完成时间
     */
    private Date completeTime;
    
    /**
     * 取消时间
     */
    private Date cancelTime;
    
    /**
     * 取消原因
     */
    private String cancelReason;
    
    /**
     * 订单备注
     */
    private String remark;
    
    /**
     * 物流公司
     */
    private String logisticsCompany;
    
    /**
     * 物流单号
     */
    private String trackingNumber;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 订单明细列表
     */
    private List<LeaseOrderItem> orderItems;
}
