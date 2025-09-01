package com.tutu.recycle.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecycleFlowDirectionEnum {
    /**
     * 入库
     */
    IN("0", "入库"),

    /**
     * 出库
     */
    OUT("1", "出库");

    /**
     * 枚举值
     */
    @EnumValue
    @JsonValue
    private final String value;

    /**
     * 枚举描述
     */
    private final String description;
}
