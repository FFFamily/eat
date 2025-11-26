package com.tutu.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountBusinessTypeEnum {
    // 供货商
    SUPPLIER("supplier"),
    // 服务商
    SERVICE_PROVIDER("service_provider")
    ;
    private final String code;

}
