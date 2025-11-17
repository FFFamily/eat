package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 交付信息DTO
 */
@Data
public class DeliveryDTO {
    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 交付时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date deliveryTime;

    /**
     * 交付方式
     */
    private String deliveryMethod;

    /**
     * 交付照片
     */
    private String deliveryPhoto;

    /**
     * 客户签名
     */
    private String partnerSignature;

    /**
     * 经办人签名
     */
    private String processorSignature;
}
