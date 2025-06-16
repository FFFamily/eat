package com.tutu.food.schema;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

// 随机食物获取参数
@Data
public class RandomFoodGetParamSchema {
    // 喜好
    private String preference;
    // 食物类型
    private String foodType;
    // 食物饮食方式
    private List<String> foodDietStyleId;
    // 饮食习惯
    private String foodHabitId;
    // 获取数量
    @NotNull(message = "获取数量不能为空")
    private Integer foodNum;
}
