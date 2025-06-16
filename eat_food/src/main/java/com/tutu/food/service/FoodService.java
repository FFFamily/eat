package com.tutu.food.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.food.entity.food.DietStyle;
import com.tutu.food.entity.food.Food;
import com.tutu.food.entity.food.FoodDietStyle;
import com.tutu.food.entity.food.FoodTypeMapping;
import com.tutu.food.entity.history.EatHistory;
import com.tutu.food.mapper.FoodMapper;
import com.tutu.food.mapper.FoodTypeMappingMapper;
import com.tutu.food.schema.FoodSchema;
import com.tutu.food.schema.RandomFoodGetParamSchema;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class FoodService extends ServiceImpl<FoodMapper, Food> {
    @Resource
    private FoodTypeMappingMapper foodTypeMappingMapper;
    @Resource
    private FoodDietStyleService foodDietStyleService;
    @Resource
    private EatHistoryService eatHistoryService;
    @Resource
    private FoodTagService foodTagService;




    /**
     * 吃掉食物
     * @param food 食物
     */
    public void eat(FoodSchema food) {
        EatHistory eatHistory = new EatHistory();
        eatHistory.setFoodName(food.getName());
        eatHistoryService.save(eatHistory);
    }

    /**
     * 根据用户获取食物
     * @param userid 用户id
     * @return 食物
     */
    public List<Food> getFoodByUserId(String userid) {
        return list(new LambdaQueryWrapper<Food>().eq(Food::getCreateBy,userid));
    }
}
