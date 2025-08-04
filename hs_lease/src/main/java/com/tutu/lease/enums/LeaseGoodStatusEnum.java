package com.tutu.lease.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LeaseGoodStatusEnum implements BaseEnum<LeaseGoodStatusEnum,String> {
    UP("up","上架"),
    DOWN("down","下架"),
    ;
    private final String code;
    private final String title;
}
