package com.tutu.food.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.food.entity.food.Food;
import com.tutu.food.entity.food.FoodDietStyle;
import com.tutu.food.entity.food.FoodTypeMapping;
import com.tutu.food.mapper.FoodTypeMappingMapper;
import com.tutu.food.schema.FoodSchema;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// 业务层食物服务类
@Service
public class BusinessFoodService {
    @Resource
    private FoodService foodService;
    @Resource
    private FoodTypeService foodTypeService;
    @Resource
    private FoodDietStyleService foodDietStyleService;
    @Resource
    private FoodTypeMappingMapper foodTypeMappingMapper;
    @Resource
    private FoodTagService foodTagService;
    /**
     * 创建食物
     * @param foodSchema 食物
     */
    @Transactional(rollbackFor = Exception.class)
    public void createFood(FoodSchema foodSchema) {
        Food oldFood = foodService.getOne(new LambdaQueryWrapper<Food>().eq(Food::getName, foodSchema.getName()));
        if (oldFood != null) {
            throw new ServiceException("食物名称已存在");
        }
        Food food = new Food();
        BeanUtils.copyProperties(foodSchema, food);
        foodService.save(food);
        List<String> foodTypeList = foodSchema.getFoodTypeList();
        // 保存食物类型
        if (foodTypeList != null && !foodTypeList.isEmpty()) {
            List<FoodTypeMapping> foodTypeMappingList = foodTypeList.stream().map(item -> {
                FoodTypeMapping foodTypeMapping = new FoodTypeMapping();
                foodTypeMapping.setFoodId(food.getId());
                foodTypeMapping.setFoodTypeId(item);
                return foodTypeMapping;
            }).toList();
            foodTypeMappingMapper.insert(foodTypeMappingList);
        }
        List<String> foodDietStyleList = foodSchema.getFoodDietStyleList();
        if (CollUtil.isNotEmpty(foodDietStyleList)) {
            List<FoodDietStyle> foodDietStyles = foodDietStyleList.stream().map(item -> {
                FoodDietStyle foodDietStyle = new FoodDietStyle();
                foodDietStyle.setFoodId(food.getId());
                foodDietStyle.setDietStyleId(item);
                return foodDietStyle;
            }).toList();
            foodDietStyleService.saveBatch(foodDietStyles);
        }
        // 绑定食物标签
        if (CollUtil.isNotEmpty(foodSchema.getFoodTagList())) {
            foodTagService.bindFoodTag(foodSchema.getFoodTagList(), food.getId());
        }
    }
}
