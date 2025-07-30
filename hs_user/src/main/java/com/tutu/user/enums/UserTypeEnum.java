package com.tutu.user.enums;

import com.tutu.common.enums.BaseEnum;
import com.tutu.common.enums.user.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserTypeEnum  implements BaseEnum<UserStatusEnum,String> {
    ADMIN("person", "个人"),
    USER("company", "企业")
    ;
    private final String code;
    private final String title;
}
