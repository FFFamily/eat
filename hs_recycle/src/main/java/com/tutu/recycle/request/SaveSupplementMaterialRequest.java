package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 补充材料保存请求
 */
@Getter
@Setter
public class SaveSupplementMaterialRequest {

    /**
     * 用户订单ID
     */
    private String orderId;

    /**
     * 交付单PDF地址
     */
    private String deliveryPdf;

    /**
     * 结算单PDF地址
     */
    private String settlementPdf;

    /**
     * 申请单PDF地址
     */
    private String applicationPdf;
}


