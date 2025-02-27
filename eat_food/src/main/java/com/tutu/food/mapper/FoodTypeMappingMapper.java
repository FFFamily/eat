package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.FoodType;
import com.tutu.food.entity.FoodTypeMapping;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodTypeMappingMapper extends BaseMapper<FoodTypeMapping> {
}
