package com.tutu.user.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RolePermissionEnum implements BaseEnum<RolePermissionEnum,String> {
    USER("user.*","用户权限"),
    ADMIN("admin.*","管理员权限")
    ;
    private final String code;
    private final String title;
}
