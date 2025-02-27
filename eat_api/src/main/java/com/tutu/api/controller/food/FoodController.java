package com.tutu.api.controller.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.Food;
import com.tutu.food.entity.FoodType;
import com.tutu.food.schema.FoodSchema;
import com.tutu.food.service.FoodService;
import com.tutu.food.service.FoodTypeService;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/food")
public class FoodController {
    @Resource
    private FoodService foodService;

    // 创建食物
    @PostMapping
    public BaseResponse<String> createFood(@RequestBody FoodSchema food) {
        foodService.createFood(food);
        return BaseResponse.success();
    }

    // 获取食物列表
    @GetMapping
    public BaseResponse<List<Food>> getAllFoods() {
        List<Food> list = foodService.list();
        return BaseResponse.success(list);
    }

    // 根据 ID 获取食物
    @GetMapping("/{id}")
    public BaseResponse<Food> getFoodById(@PathVariable("id") String id) {
        Food food = foodService.getById(id);
        return  BaseResponse.success(food);
    }

    // 更新食物
    @PutMapping("")
    public BaseResponse<String> updateFood( @RequestBody Food food) {
        foodService.updateById(food);
        return BaseResponse.success();
    }

    // 删除食物
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteFood(@PathVariable("id") String id) {
        foodService.removeById(id);
        return BaseResponse.success();
    }

    /**
     * 随机获取
     * @return 食物
     */
    @PostMapping("/random")
    public BaseResponse<List<Food>> getRandomFood(){
        return  BaseResponse.success(foodService.getRandomFood());
    }

}
