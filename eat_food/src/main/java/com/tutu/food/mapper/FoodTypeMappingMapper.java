package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.food.FoodTypeMapping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoodTypeMappingMapper extends BaseMapper<FoodTypeMapping> {

}
