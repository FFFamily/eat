package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.habit.FoodHabit;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodHabitMapper extends BaseMapper<FoodHabit> {
}