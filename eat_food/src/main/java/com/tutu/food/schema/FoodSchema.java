package com.tutu.food.schema;

import com.tutu.food.entity.Food;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FoodSchema extends Food {
    // 食物类型列表
    private List<String> foodTypeList;
}
