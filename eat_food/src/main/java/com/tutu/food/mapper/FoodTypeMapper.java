package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.food.FoodType;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodTypeMapper extends BaseMapper<FoodType> {
}
