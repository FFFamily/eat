package com.tutu.recycle.response;

import lombok.Data;

@Data
public class WxTransportOrderListResponse {
    private String id;
    // 识别码
    private String code;
    // 取货地址
    private String pickupAddress;
    // 送达地址
    private String deliveryAddress;
    // 公司
    private String company;
    // 联系人
    private String processor;
    // 联系人手机号
    private String processorPhone;
}
