package com.tutu.food.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.food.Food;
import com.tutu.food.entity.food.FoodTypeMapping;
import com.tutu.food.mapper.FoodMapper;
import com.tutu.food.mapper.FoodTypeMappingMapper;
import com.tutu.food.schema.FoodSchema;
import com.tutu.food.schema.RandomFoodGetParamSchema;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService extends ServiceImpl<FoodMapper, Food> {
    @Resource
    private FoodTypeMappingMapper foodTypeMappingMapper;
    /**
     * 随机获取食物
     * @return 食物
     */
    public List<Food> getRandomFood(RandomFoodGetParamSchema param) {
        // 获取总记录数
        long totalCount = count();
        // 随机数
        long randomNum = RandomUtil.randomLong(0, totalCount);
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<Food>().last("LIMIT " + randomNum + ", 1");
        return list(wrapper);
    }
    /**
     * 创建食物
     * @param foodSchema 食物
     */
    public void createFood(FoodSchema foodSchema) {
        Food food = new Food();
        BeanUtils.copyProperties(foodSchema, food);
        save(food);
        List<String> foodTypeList = foodSchema.getFoodTypeList();
        if (foodTypeList != null && !foodTypeList.isEmpty()) {
            List<FoodTypeMapping> foodTypeMappingList = foodTypeList.stream().map(item -> {
                FoodTypeMapping foodTypeMapping = new FoodTypeMapping();
                foodTypeMapping.setFoodId(food.getId());
                foodTypeMapping.setFoodTypeId(item);
                return foodTypeMapping;
            }).toList();
            foodTypeMappingMapper.insert(foodTypeMappingList);
        }

    }
}
