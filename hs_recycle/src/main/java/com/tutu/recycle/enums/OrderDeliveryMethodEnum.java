package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderDeliveryMethodEnum {
    // 线上交付
    ONLINE("online", "线上交付"),
    // 线下交付
    OFFLINE("offline", "线下交付")
    ;
    private final String code;
    private final String title;
}
