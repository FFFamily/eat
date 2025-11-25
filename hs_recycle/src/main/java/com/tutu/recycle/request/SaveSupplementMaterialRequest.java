package com.tutu.recycle.request;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class SaveSupplementMaterialRequest {
    private String orderId;
    // 结算单PDF URL
    private String settlementPdf;
    // 交付单PDF URL
    private String deliveryPdf;
    // 申请单PDF URL
    private String applicationPdf;
}
