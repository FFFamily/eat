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
    private FoodMapper foodMapper;

    /**
     * 随机获取食物
     * @return 食物
     */
    public List<Food> getRandomFood(RandomFoodGetParamSchema param) {
        assert param != null;
        // 获取总记录数
        long totalCount;
        LambdaQueryWrapper<FoodDietStyle> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodDietStyle::getCreateBy, StpUtil.getLoginIdAsString());
        if (param.getFoodDietStyleId() == null) {
            totalCount = foodDietStyleService.count(wrapper);
        }else {
            totalCount = foodDietStyleService.count(
                    wrapper.in(FoodDietStyle::getDietStyleId, param.getFoodDietStyleId())
            );
        }
        if(totalCount == 0){
            return new ArrayList<>();
        }
        // 随机数
        long randomNum = RandomUtil.randomLong(0, totalCount);
        List<FoodDietStyle> foodDietStyleList = foodDietStyleService.list(new LambdaQueryWrapper<FoodDietStyle>()
                .eq(FoodDietStyle::getCreateBy, StpUtil.getLoginIdAsString())
                .last("LIMIT " + randomNum + ", "+param.getFoodNum())
        );
        return list(new LambdaQueryWrapper<Food>().in(Food::getId, foodDietStyleList.stream().map(FoodDietStyle::getFoodId).toList()));
    }
    /**
     * 创建食物
     * @param foodSchema 食物
     */
    @Transactional(rollbackFor = Exception.class)
    public void createFood(FoodSchema foodSchema) {
        Food oldFood = getOne(new LambdaQueryWrapper<Food>().eq(Food::getName, foodSchema.getName()));
        if (oldFood != null) {
            throw new ServiceException("食物名称已存在");
        }
        Food food = new Food();
        BeanUtils.copyProperties(foodSchema, food);
        save(food);
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

    }

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
