package com.tutu.api.controller.admin.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.food.FoodType;
import com.tutu.food.service.FoodTypeService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/foodType")
public class FoodTypeController {
    @Resource
    private FoodTypeService foodTypeService;
    // 获取食物类型列表
    @PostMapping("/list")
    public BaseResponse<List<FoodType>> list() {
        return BaseResponse.success(foodTypeService.list());
    }
    // 添加食物类型
    @PostMapping("/addOrUpdate")
    public BaseResponse<Void> addOrUpdate(@RequestBody FoodType foodType) {
        foodTypeService.saveOrUpdateFoodType(foodType);
        return BaseResponse.success();
    }
    // 删除
    @DeleteMapping("/del/{id}")
    public BaseResponse<Void> delete(@PathVariable String id) {
        foodTypeService.removeById(id);
        return BaseResponse.success();
    }
}
