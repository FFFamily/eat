package com.tutu.api.controller.mobile.food;

import com.tutu.common.Response.BaseResponse;
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
    public BaseResponse<Void> createFoodHabit(@RequestBody FoodHabit foodHabit) {
        foodHabitService.createFoodHabit(foodHabit);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 获取食物习惯
     * @param id 食物习惯 ID
     * @return 食物习惯实体
     */
    @GetMapping("/{id}")
    public BaseResponse<FoodHabit> getFoodHabitById(@PathVariable String id) {
        return BaseResponse.success(foodHabitService.getFoodHabitById(id));
    }

    /**
     * 获取所有食物习惯
     * @return 食物习惯列表
     */
    @GetMapping("/all")
    public BaseResponse<List<FoodHabit>> getAllFoodHabits() {
        return BaseResponse.success(foodHabitService.getAllFoodHabits());
    }

    /**
     * 更新食物习惯
     * @param foodHabit 食物习惯实体
     * @return 更新成功返回 true，失败返回 false
     */
    @PutMapping
    public BaseResponse<Void> updateFoodHabit(@RequestBody FoodHabit foodHabit) {
        foodHabitService.updateFoodHabit(foodHabit);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 删除食物习惯
     * @param id 食物习惯 ID
     * @return 删除成功返回 true，失败返回 false
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteFoodHabitById(@PathVariable String id) {
        foodHabitService.deleteFoodHabitById(id);
        return BaseResponse.success();
    }
}
