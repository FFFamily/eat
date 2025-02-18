package com.tutu.api.controller;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.entity.User;
import com.tutu.user.request.LoginRequest;
import com.tutu.user.response.LoginUserResponse;
import com.tutu.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/admin/auth")
public class LoginController {
    @Resource
    private UserService userService;

    @GetMapping("/test")
    public BaseResponse<String> test() {
        return BaseResponse.success();
    }


    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        // 获取用户信息
        User user = userService.getUserByUsername(loginRequest.getUsername());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        // 验证密码（假设密码已经加密存储）
        String md5 = SaSecureUtil.md5(loginRequest.getPassword());
        if (!md5.equals(user.getPassword())){
            return BaseResponse.error("账号密码错误");
        }
        // 检查用户状态
//        if (!"1".equals(user.getStatus())) {
//            return BaseResponse.error("账号已被禁用");
//        }
        // 登录
        StpUtil.login(user.getId());
        // 返回token
        return BaseResponse.success(StpUtil.getTokenValue());
    }
    
    @PostMapping("/logout")
    public BaseResponse<Void> logout() {
        StpUtil.logout();
        return BaseResponse.success();
    }
    

    @GetMapping("/getLoginInfo")
    public BaseResponse<LoginUserResponse> getUserInfo() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        LoginUserResponse userInfo = new LoginUserResponse();
        BeanUtil.copyProperties(user, userInfo);
        return BaseResponse.success(userInfo);
    }

    

}
