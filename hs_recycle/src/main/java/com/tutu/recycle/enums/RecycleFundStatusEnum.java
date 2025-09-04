package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecycleFundStatusEnum {
    WAIT("wait", "待确认"),
    CONFIRM("confirm", "已确认")
    ;
    private final String code;
    private final String title;
}
