package com.tutu.eat.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/eat")
public class LoginController {
    @Resource
    private UserService userService;
    // 会话登录接口
    @RequestMapping("/login")
    public BaseResponse doLogin(String username, String pwd) {
        User oldUser = userService.getUserByUsername(username);
        // 第一步：比对前端提交的账号名称、密码
        if("zhang".equals(username) && "123456".equals(pwd)) {
            // 第二步：根据账号id，进行登录
            StpUtil.login(10001);
            return BaseResponse.success("登录成功");
        }
        return BaseResponse.error("登录失败");
    }

}
