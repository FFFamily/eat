package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.tag.FoodTag;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodTagMapper extends BaseMapper<FoodTag> {
}
