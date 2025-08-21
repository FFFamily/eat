package com.tutu.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserUseTypeEnum {
    // 普通用户
    NORMAL("user", "客户"),
    // 运输专人
    TRANSPORT("transport", "运输"),
    // 运营
    OPERATOR("operator", "运营"),
    // 分拣
    SORTING("sorting", "分拣"),
    // 库管
    STORAGE("storage", "库管"),
    // 财务
    FINANCE("finance", "财务");
    
    private final String code;
    private final String title;
}
