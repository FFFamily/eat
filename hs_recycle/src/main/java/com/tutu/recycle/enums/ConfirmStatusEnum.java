package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ConfirmStatusEnum {
    NOT_CONFIRMED("not_confirmed", "未确认"),
    CONFIRMED("confirmed", "已确认"),
    REJECTED("rejected", "已驳回")
    ;
    private final String code;
    private final String title;
}
