package com.tutu.recycle.response;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 分拣交付大厅订单响应
 */
@Data
public class SortingDeliveryHallResponse {
    /**
     * 用户订单ID
     */
    private String id;
    /**
     * 运输方式
     */
    private String transportMethod;
    /**
     * 用户订单编号
     */
    private String no;

    /**
     * 合作方公司
     */
    private String company;

    /**
     * 取货地址（来源站点）
     */
    private String pickupAddress;

    /**
     * 送达地址
     */
    private String deliveryAddress;

    /**
     * 联系人
     */
    private String processor;

    /**
     * 联系人手机号
     */
    private String processorPhone;

    // 订单识别码
    private String identifyCode;
    // 重量
    private BigDecimal goodsWeight;
    // 结束时间
    private String endTime;
}


