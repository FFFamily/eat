package com.tutu.food.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.food.DietStyle;
import com.tutu.food.entity.food.FoodDietStyle;
import com.tutu.food.mapper.FoodDietStyleMapper;
import org.springframework.stereotype.Service;

@Service
public class FoodDietStyleService extends ServiceImpl<FoodDietStyleMapper, FoodDietStyle> {
}