package com.tutu.api.controller.mobile.food;

import com.tutu.food.entity.habit.FoodHabit;
import com.tutu.food.service.FoodHabitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food/habit")
public class FoodHabitController {

    @Autowired
    private FoodHabitService foodHabitService;

    /**
     * 创建食物习惯
     * @param foodHabit 食物习惯实体
     * @return 创建成功返回 true，失败返回 false
     */
    @PostMapping
    public boolean createFoodHabit(@RequestBody FoodHabit foodHabit) {
        return foodHabitService.createFoodHabit(foodHabit);
    }

    /**
     * 根据 ID 获取食物习惯
     * @param id 食物习惯 ID
     * @return 食物习惯实体
     */
    @GetMapping("/{id}")
    public FoodHabit getFoodHabitById(@PathVariable String id) {
        return foodHabitService.getFoodHabitById(id);
    }

    /**
     * 获取所有食物习惯
     * @return 食物习惯列表
     */
    @GetMapping("/all")
    public List<FoodHabit> getAllFoodHabits() {
        return foodHabitService.getAllFoodHabits();
    }

    /**
     * 更新食物习惯
     * @param foodHabit 食物习惯实体
     * @return 更新成功返回 true，失败返回 false
     */
    @PutMapping
    public boolean updateFoodHabit(@RequestBody FoodHabit foodHabit) {
        return foodHabitService.updateFoodHabit(foodHabit);
    }

    /**
     * 根据 ID 删除食物习惯
     * @param id 食物习惯 ID
     * @return 删除成功返回 true，失败返回 false
     */
    @DeleteMapping("/{id}")
    public boolean deleteFoodHabitById(@PathVariable String id) {
        return foodHabitService.deleteFoodHabitById(id);
    }
}
