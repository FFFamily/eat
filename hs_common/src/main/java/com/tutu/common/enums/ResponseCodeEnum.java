package com.tutu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCodeEnum {
    SUCCESS(200,"成功"),
    ERROR(500,"失败")
    ;
    private final int code;
    private final String msg;
}
