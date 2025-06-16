package com.tutu.food.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.habit.FoodHabit;
import com.tutu.food.mapper.FoodHabitMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FoodHabitService extends ServiceImpl<FoodHabitMapper, FoodHabit>  {

    
    public boolean createFoodHabit(FoodHabit foodHabit) {
        return save(foodHabit);
    }

    
    public FoodHabit getFoodHabitById(String id) {
        return getById(id);
    }

    
    public List<FoodHabit> getAllFoodHabits() {
        return list();
    }

    
    public boolean updateFoodHabit(FoodHabit foodHabit) {
        return updateById(foodHabit);
    }

    
    public boolean deleteFoodHabitById(String id) {
        return removeById(id);
    }
}
