package com.tutu.api.controller.mobile.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.tag.FoodTag;
import com.tutu.food.service.FoodTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/foodTag")
public class UserFoodTagController {

    @Autowired
    private FoodTagService foodTagService;

    /**
     * 创建食物标签
     * @param foodTag 食物标签实体
     * @return 创建成功返回包含 true 的 BaseResponse，失败返回包含 false 的 BaseResponse
     */
    @PostMapping("/create")
    public BaseResponse<Boolean> createFoodTag(@RequestBody FoodTag foodTag) {
        return BaseResponse.success(foodTagService.createFoodTag(foodTag));
    }

    /**
     * 根据 ID 获取食物标签
     * @param id 食物标签 ID
     * @return 包含食物标签实体的 BaseResponse
     */
    @GetMapping("/{id}")
    public BaseResponse<FoodTag> getFoodTagById(@PathVariable String id) {
        return BaseResponse.success(foodTagService.getFoodTagById(id));
    }

    /**
     * 获取所有食物标签
     * @return 包含食物标签列表的 BaseResponse
     */
    @GetMapping("/all")
    public BaseResponse<List<FoodTag>> getAllFoodTags() {
        return BaseResponse.success(foodTagService.getAllFoodTags());
    }

    /**
     * 更新食物标签
     * @param foodTag 食物标签实体
     * @return 更新成功返回包含 true 的 BaseResponse，失败返回包含 false 的 BaseResponse
     */
    @PutMapping
    public BaseResponse<Boolean> updateFoodTag(@RequestBody FoodTag foodTag) {
        return BaseResponse.success(foodTagService.updateFoodTag(foodTag));
    }

    /**
     * 根据 ID 删除食物标签
     * @param id 食物标签 ID
     * @return 删除成功返回包含 true 的 BaseResponse，失败返回包含 false 的 BaseResponse
     */
    @DeleteMapping("/del/{id}")
    public BaseResponse<Boolean> deleteFoodTagById(@PathVariable String id) {
        return BaseResponse.success(foodTagService.deleteFoodTagById(id));
    }

}