package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 运输单展示 DTO
 */
@Data
public class TransportOrderDTO {
    /**
     * 订单ID
     */
    private String id;

    /**
     * 订单识别码
     */
    private String identifyCode;

    /**
     * 订单编号
     */
    private String no;

    /**
     * 取货地址
     */
    private String pickupAddress;

    /**
     * 送达地址
     */
    private String deliveryAddress;

    /**
     * 客户公司名称
     */
    private String contractPartnerName;

    /**
     * 客户联系人（经办人）
     */
    private String processor;

    /**
     * 联系电话
     */
    private String processorPhone;

    /**
     * 运输方式
     */
    private String transportMethod;

    /**
     * 运输状态
     */
    private String transportStatus;

    /**
     * 抢单时间（开始时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 交付时间（主订单的交付时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;

    /**
     * 送达时间（结束时间）
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 父订单ID
     */
    private String parentId;
}
