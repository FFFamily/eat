package com.tutu.api.controller.mobile.food;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/user/food")
public class UserFoodController {
}
