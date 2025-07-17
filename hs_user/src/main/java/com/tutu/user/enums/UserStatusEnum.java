package com.tutu.user.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 用户状态枚举
 */
@Getter
@AllArgsConstructor
public enum UserStatusEnum implements BaseEnum<UserStatusEnum,String> {
    USE("use","正常"),
    DISABLE("disable","禁用")
    ;
    private final String code;
    private final String title;
}
