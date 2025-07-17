package com.tutu.user.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 基础来源枚举
 */
@Getter
@AllArgsConstructor
public enum BaseOriginEnum implements BaseEnum<UserStatusEnum,String> {
    ADMIN("system", "系统内部"),
    USER("custom", "用户自定义")
    ;
    private final String code;
    private final String title;
}
