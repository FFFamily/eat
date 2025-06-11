package com.tutu.api.controller.mobile.food;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.food.Food;
import com.tutu.food.schema.FoodSchema;
import com.tutu.food.schema.RandomFoodGetParamSchema;
import com.tutu.food.service.BusinessFoodService;
import com.tutu.food.service.FoodService;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/user/food")
public class UserFoodController {
    @Resource
    private FoodService foodService;
    @Resource
    private BusinessFoodService businessFoodService;
    public BaseResponse<List<Food>> eatFood(){
        return BaseResponse.success();
    }
    // 创建食物
    @PostMapping("/create")
    public BaseResponse<String> createFood(@RequestBody FoodSchema food) {
        businessFoodService.createFood(food);
        return BaseResponse.success();
    }
    // 获取食物列表
    @GetMapping
    public BaseResponse<List<Food>> getAllFoods() {
        String userid = StpUtil.getLoginIdAsString();
        List<Food> list = foodService.getFoodByUserId(userid);
        return BaseResponse.success(list);
    }
    // 根据 ID 获取食物
    @GetMapping("/{id}")
    public BaseResponse<Food> getFoodById(@PathVariable("id") String id) {
        Food food = foodService.getById(id);
        return  BaseResponse.success(food);
    }
    // 更新食物
    @PutMapping()
    public BaseResponse<String> updateFood(@RequestBody Food food) {
        foodService.updateById(food);
        return BaseResponse.success();
    }
    // 删除食物
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteFood(@PathVariable("id") String id) {
        foodService.removeById(id);
        return BaseResponse.success();
    }
    // 随机获取
    @PostMapping("/recommendFood")
    public BaseResponse<List<Food>> getRandomFood(@RequestBody @Validated RandomFoodGetParamSchema param){
        return  BaseResponse.success(foodService.getRandomFood(param));
    }

    // 吃食物ß
    @PostMapping("/eat")
    public BaseResponse<Void> eatFood(@RequestBody FoodSchema food) {
        foodService.eat(food);
        return BaseResponse.success();
    }


}
