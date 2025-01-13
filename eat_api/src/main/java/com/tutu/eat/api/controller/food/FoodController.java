package com.tutu.eat.api.controller.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.Food;
import com.tutu.food.entity.FoodType;
import com.tutu.food.service.FoodService;
import com.tutu.food.service.FoodTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/food")
public class FoodController {
    @Resource
    private FoodService foodService;
    /**
     * 添加食物类型
     */
    @PostMapping("/addOrUpdate")
    public BaseResponse<Void> addOrUpdate(@RequestBody Food food) {
        return BaseResponse.success();
    }
}
