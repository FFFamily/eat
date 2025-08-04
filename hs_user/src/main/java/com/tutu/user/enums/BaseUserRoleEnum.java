package com.tutu.user.enums;

import com.tutu.common.enums.BaseEnum;
import com.tutu.common.enums.user.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum BaseUserRoleEnum implements BaseEnum<BaseUserRoleEnum,String> {
    ADMIN("admin", "管理员"),
    USER("user", "普通用户")
    ;
    private final String code;
    private final String title;
}
