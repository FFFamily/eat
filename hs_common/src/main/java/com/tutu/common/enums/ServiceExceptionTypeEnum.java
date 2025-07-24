package com.tutu.common.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * 系统内部异常枚举
 */
@Getter
@AllArgsConstructor
public enum ServiceExceptionTypeEnum {
    DEFAULT_ERROR("服务错误"),
    DATA_ERROR("数据错误")
    ;
    private final String title;
}
