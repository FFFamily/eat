package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.Food;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodMapper extends BaseMapper<Food> {
}
