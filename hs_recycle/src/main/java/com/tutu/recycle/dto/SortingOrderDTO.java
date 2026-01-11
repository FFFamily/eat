package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 分拣订单详情 DTO
 */
@Data
public class SortingOrderDTO {
    /**
     * 订单ID
     */
    private String id;

    /**
     * 父订单ID
     */
    private String parentId;
    private String parentCode;

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
     * 分拣状态
     */
    private String sortingStatus;

    /**
     * 分拣开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startTime;

    /**
     * 分拣完成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    /**
     * 主订单交付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;

    /**
     * 分拣图片/凭证
     */
    private String orderNodeImg;
}


