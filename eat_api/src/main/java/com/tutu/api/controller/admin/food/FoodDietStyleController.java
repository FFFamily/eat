package com.tutu.api.controller.admin.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.PermissionRequired;
import com.tutu.food.entity.food.DietStyle;
import com.tutu.food.service.DietStyleService;
import com.tutu.food.service.FoodDietStyleService;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/dietStyle")
public class FoodDietStyleController {

    @Autowired
    private DietStyleService dietStyleService;

    // 添加食物饮食方式
    @PostMapping("/add")
    public BaseResponse<Void> add(@RequestBody DietStyle dietStyle) {
        dietStyleService.save(dietStyle);
        return BaseResponse.success();
    }

    // 根据 ID 删除食物饮食方式
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        dietStyleService.removeById(id);
        return BaseResponse.success();
    }

    // 更新食物饮食方式
    @PutMapping("/update")
    public BaseResponse<Void> update(@RequestBody DietStyle dietStyle) {
        dietStyleService.updateById(dietStyle);
        return BaseResponse.success();
    }

    // 根据 ID 查询食物饮食方式
    @GetMapping("/get/{id}")
    public BaseResponse<DietStyle>  get(@PathVariable Long id) {
        return BaseResponse.success(dietStyleService.getById(id));
    }

    // 查询所有食物饮食方式
    @GetMapping("/all")
    @PermissionRequired("admin.dietStyle.all")
    public BaseResponse<List<DietStyle>> list() {
        return BaseResponse.success(dietStyleService.list());
    }

}