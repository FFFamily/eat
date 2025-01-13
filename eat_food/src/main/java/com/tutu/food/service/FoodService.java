package com.tutu.food.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.Food;
import com.tutu.food.mapper.FoodMapper;
import org.springframework.stereotype.Service;

@Service
public class FoodService extends ServiceImpl<FoodMapper, Food> {
}
