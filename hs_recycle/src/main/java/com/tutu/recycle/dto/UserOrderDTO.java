package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.recycle.entity.order.RecycleOrderItem;
import com.tutu.recycle.entity.user.UserOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 用户订单DTO，继承UserOrder并包含所有订单类型需要的字段
 */
@Getter
@Setter
public class UserOrderDTO extends UserOrder {

    // 采购订单图片
    private String orderNodeImg;
    // 经办人id
    private String processorId;
    // 经办人名称
    private String processorName;
    /**
     * 走款账号
     */
    private String paymentAccount;
    
    /**
     * 仓库地址
     */
    private String warehouseAddress;

    
    /**
     * 流转方向
     */
    private String flowDirection;
    
    /**
     * 合同金额
     */
    private BigDecimal contractPrice;
    /**
     * 上传时间
     */
    private Date uploadTime;
    // 运输相关 =======
    // 运输开始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;
     // 运输结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;
    // 运输起点
    private String pickupAddress;
    // 运输终点/采购的采购地址
    private String deliveryAddress;
    // 运输货物重量
     private BigDecimal goodsWeight;

     //仓库相关=====
    private String warehouseId;
     // 货物明细
     private List<RecycleOrderItem> items;

     // 采购订单相关=====
    // 站点
    private String siteName;
}

