package com.tutu.admin_user.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdUserRoleEnum implements BaseEnum<AdUserRoleEnum,String> {
    ADMIN("admin", "管理员"),
    USER("user", "普通用户")
    ;
    private final String code;
    private final String title;
}
