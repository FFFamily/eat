package com.tutu.admin_user.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdUserStatusEnum implements BaseEnum<AdUserStatusEnum,String> {
    ENABLE("enable", "启用"),
    DISABLE("disable", "禁用");
    private final String code;
    private final String title;
}
