package com.tutu.eat.api.controller.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.FoodType;
import com.tutu.food.service.FoodTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/foodType")
public class FoodTypeController {
    @Resource
    private FoodTypeService foodTypeService;
    /**
     * 食物类型列表
     */
    @PostMapping("/list")
    public BaseResponse<List<FoodType>> list() {
        return BaseResponse.success(foodTypeService.list());
    }

    /**
     * 添加食物类型
     */
    public BaseResponse<Void> addOrUpdate(@RequestBody FoodType foodType) {
        foodTypeService.saveOrUpdateFoodType(foodType);
        return BaseResponse.success();
    }
}
