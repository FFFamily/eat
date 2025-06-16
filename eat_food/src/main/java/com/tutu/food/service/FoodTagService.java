package com.tutu.food.service;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.tag.FoodTag;

import com.tutu.food.entity.tag.FoodTagMapping;
import com.tutu.food.mapper.FoodTagMapper;
import com.tutu.food.mapper.FoodTagMappingMapper;
import jakarta.annotation.Resource;
import lombok.Getter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Getter
@Service
public class FoodTagService extends ServiceImpl<FoodTagMapper, FoodTag> {
    @Resource
    private FoodTagMappingMapper foodTagMappingMapper;

    public boolean createFoodTag(FoodTag foodTag) {
        return save(foodTag);
    }

    
    public FoodTag getFoodTagById(String id) {
        return getById(id);
    }

    /**
     * 获取所有食物标签
     * @return 食物标签列表
     */
    public List<FoodTag> getAllFoodTags() {
        return list();
    }

    
    public boolean updateFoodTag(FoodTag foodTag) {
        return updateById(foodTag);
    }

    
    public boolean deleteFoodTagById(String id) {
        return removeById(id);
    }

    /**
     * 绑定食物标签
     * @param foodTagList 标签列表
     * @param foodId 食物id
     */
    @Transactional(rollbackFor = Exception.class)
    public void bindFoodTag(List<String> foodTagList, String foodId) {
        // 1. 先删除原来的标签
        foodTagMappingMapper.delete(new LambdaQueryWrapper<FoodTagMapping>().eq(FoodTagMapping::getFoodId, foodId));
        // 2. 再添加新的标签
        List<FoodTagMapping> foodTagMappings = foodTagList.stream().map(i -> {
            FoodTagMapping foodTagMapping = new FoodTagMapping();
            foodTagMapping.setFoodId(foodId);
            foodTagMapping.setTagId(i);
            return foodTagMapping;
        }).toList();
        foodTagMappingMapper.insert(foodTagMappings);
    }

    /**
     * 根据食物id删除标签
     * @param foodId 食物id
     */
    public void deleteFoodTagByFoodId(String foodId) {
        foodTagMappingMapper.delete(new LambdaQueryWrapper<FoodTagMapping>().eq(FoodTagMapping::getFoodId, foodId));
    }

    /**
     * 根据标签查询对应的食物数量
     * @param tags
     * @return
     */
    public long getOwnerFoodCountByTag(List<String> tags) {
        String userId = StpUtil.getLoginIdAsString();
        return foodTagMappingMapper.getOwnerFoodCountByTag(tags,userId);
    }
}
