package com.tutu.recycle.response;

import lombok.Data;

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
    private String contactName;

    /**
     * 联系人手机号
     */
    private String contactPhone;

    // 订单识别码
    private String identifyCode;
}


