package com.tutu.api.controller.mobile.food;

import com.tutu.common.Response.BaseResponse;
import com.tutu.food.entity.food.Food;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/user/food")
public class UserFoodController {

    public BaseResponse<List<Food>> eatFood(){
        return  BaseResponse.success();
    }
}
