package com.tutu.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserUseTypeEnum {
    // 普通用户
    NORMAL("user", "普通用户"),
    // 运输专人
    TRANSPORT("transport", "运输专人");
    private final String code;
    private final String title;
}
