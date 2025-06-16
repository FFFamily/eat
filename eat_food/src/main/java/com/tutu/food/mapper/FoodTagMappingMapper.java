package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.tag.FoodTagMapping;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FoodTagMappingMapper extends BaseMapper<FoodTagMapping> {
    // 根据标签获取对应的食物数量
    long getOwnerFoodCountByTag(List<String> tags, String userId);
}
