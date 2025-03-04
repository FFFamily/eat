package com.tutu.food.entity.food;

import lombok.Data;

/**
 * 食物类型中间表
 */
@Data
public class FoodTypeMapping {
    // 编号
    private String id;
    // 食物id
    private String foodId;
    // 食物类型id
    private String foodTypeId;
}
