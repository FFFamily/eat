package com.tutu.food.enums;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FoodStatusEnum implements BaseEnum<FoodStatusEnum,String> {
    USE("use","正常"),
    DISABLE("disable","禁用")
    ;
    private final String code;
    private final String name;
}
