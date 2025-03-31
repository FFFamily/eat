package com.tutu.food.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
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
        String name = foodType.getName();
        FoodType oldFoodType = foodTypeMapper.selectOne(new LambdaQueryWrapper<FoodType>().eq(FoodType::getName, name));
        if (oldFoodType != null) {
            throw new ServiceException("已存在相同名称的食物类型："+name);
        }
        if (id == null) {
            foodTypeMapper.insert(foodType);
        } else {
            foodTypeMapper.updateById(foodType);
        }
    }
}
