package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.food.Food;
import com.tutu.food.schema.RandomFoodGetParamSchema;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {

    public List<Food> getRandomFood(long randomNum, RandomFoodGetParamSchema food);
}
