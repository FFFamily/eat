package com.tutu.food.schema;

import com.tutu.food.entity.food.Food;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodSchema extends Food {
    // 食物类型列表
    private List<String> foodTypeList;
    private List<String> foodTagList;
    private List<String> foodDietStyleList;
}
