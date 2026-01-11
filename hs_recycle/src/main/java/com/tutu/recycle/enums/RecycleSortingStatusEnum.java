package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecycleSortingStatusEnum {
    // 待定
    PENDING("pending"),
    // 完成
    COMPLETE("complete")
    ;
    private final String status;
}
