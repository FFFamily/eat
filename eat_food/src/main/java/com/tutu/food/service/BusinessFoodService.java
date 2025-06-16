package com.tutu.food.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.food.entity.food.Food;
import com.tutu.food.entity.food.FoodDietStyle;
import com.tutu.food.entity.food.FoodTypeMapping;
import com.tutu.food.entity.habit.FoodHabit;
import com.tutu.food.entity.tag.FoodTag;
import com.tutu.food.entity.tag.FoodTagMapping;
import com.tutu.food.mapper.FoodTypeMappingMapper;
import com.tutu.food.schema.FoodSchema;
import com.tutu.food.schema.RandomFoodGetParamSchema;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Resource
    private FoodHabitService foodHabitService;
    /**
     * 创建食物
     * @param foodSchema 食物
     */
    @Transactional(rollbackFor = Exception.class)
    public void createFood(FoodSchema foodSchema) {
        Food oldFood = foodService.getOne(
                new LambdaQueryWrapper<Food>().eq(Food::getName, foodSchema.getName())
                        .eq(Food::getCreateBy, StpUtil.getLoginIdAsString())
        );
        if (oldFood != null) {
            throw new ServiceException("食物名称已存在");
        }
        Food food = new Food();
        BeanUtils.copyProperties(foodSchema, food);
        foodService.save(food);
//        List<String> foodTypeList = foodSchema.getFoodTypeList();
//        // 保存食物类型
//        if (foodTypeList != null && !foodTypeList.isEmpty()) {
//            List<FoodTypeMapping> foodTypeMappingList = foodTypeList.stream().map(item -> {
//                FoodTypeMapping foodTypeMapping = new FoodTypeMapping();
//                foodTypeMapping.setFoodId(food.getId());
//                foodTypeMapping.setFoodTypeId(item);
//                return foodTypeMapping;
//            }).toList();
//            foodTypeMappingMapper.insert(foodTypeMappingList);
//        }
//        List<String> foodDietStyleList = foodSchema.getFoodDietStyleList();
//        if (CollUtil.isNotEmpty(foodDietStyleList)) {
//            List<FoodDietStyle> foodDietStyles = foodDietStyleList.stream().map(item -> {
//                FoodDietStyle foodDietStyle = new FoodDietStyle();
//                foodDietStyle.setFoodId(food.getId());
//                foodDietStyle.setDietStyleId(item);
//                return foodDietStyle;
//            }).toList();
//            foodDietStyleService.saveBatch(foodDietStyles);
//        }
        // 绑定食物标签
        if (CollUtil.isNotEmpty(foodSchema.getFoodTagList())) {
            foodTagService.bindFoodTag(foodSchema.getFoodTagList(), food.getId());
        }
    }
    /**
     * 删除食物
     * @param foodId 食物ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFood(String foodId) {
        Food food = foodService.getById(foodId);
        assert food != null;
        foodService.removeById(foodId);
        // 删除食物绑定的标签
        foodTagService.deleteFoodTagByFoodId(foodId);
    }

    /**
     * 随机获取食物
     * @return 食物
     */
    public List<Food> getRandomFood(RandomFoodGetParamSchema param) {
        assert param != null;
        // 获取总记录数
        long totalCount;
        LambdaQueryWrapper<FoodTag> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FoodTag::getCreateBy, StpUtil.getLoginIdAsString());
        if (param.getFoodHabitId() == null) {
            totalCount = foodTagService.count(wrapper);
        }else {
            FoodHabit foodHabit = foodHabitService.getFoodHabitById(param.getFoodHabitId());
            List<String> tags = foodHabit.getTag().getTags();
            totalCount = foodTagService.getOwnerFoodCountByTag(tags);
        }
        if(totalCount == 0){
            return new ArrayList<>();
        }
        // 随机数
        long randomNum = RandomUtil.randomLong(0, totalCount);
        List<FoodTagMapping> foodDietStyleList = foodTagService.getFoodTagMappingMapper().selectList(new LambdaQueryWrapper<FoodTagMapping>()
                .eq(FoodTagMapping::getCreateBy, StpUtil.getLoginIdAsString())
                .last("LIMIT " + randomNum + ", "+param.getFoodNum())
        );
        return foodService.list(new LambdaQueryWrapper<Food>().in(Food::getId, foodDietStyleList.stream().map(FoodTagMapping::getId).toList()));
    }

}
