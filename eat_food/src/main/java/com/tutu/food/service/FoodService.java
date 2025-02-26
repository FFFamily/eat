package com.tutu.food.service;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.Food;
import com.tutu.food.mapper.FoodMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodService extends ServiceImpl<FoodMapper, Food> {
    /**
     * 随机获取食物
     * @return 食物
     */
    public List<Food> getRandomFood() {
        // 获取总记录数
        long totalCount = count();
        // 随机数
        long randomNum = RandomUtil.randomLong(0, totalCount);
        LambdaQueryWrapper<Food> wrapper = new LambdaQueryWrapper<Food>().last("LIMIT " + randomNum + ", 1");
        return list(wrapper);
    }
}
