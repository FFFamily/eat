package com.tutu.food.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.food.FoodType;
import com.tutu.food.mapper.FoodTypeMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class FoodTypeService extends ServiceImpl<FoodTypeMapper, FoodType> {
    @Resource
    private FoodTypeMapper foodTypeMapper;
    /**
     * 添加食物类型
     */
    public void saveOrUpdateFoodType(FoodType foodType) {
        String id = foodType.getId();
        if (id == null) {
            foodTypeMapper.insert(foodType);
        } else {
            foodTypeMapper.updateById(foodType);
        }
    }
}
