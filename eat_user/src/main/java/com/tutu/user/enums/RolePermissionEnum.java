package com.tutu.user.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RolePermissionEnum implements BaseEnum<UserStatusEnum,String> {

    ;
    private final String code;
    private final String title;
}
