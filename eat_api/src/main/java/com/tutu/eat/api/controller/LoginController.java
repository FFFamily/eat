package com.tutu.eat.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginController {
    @Resource
    private UserService userService;
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody LoginDTO loginDTO) {
        // 参数校验
        if (StrUtil.isBlank(loginDTO.getUsername()) || StrUtil.isBlank(loginDTO.getPassword())) {
            return BaseResponse.fail("用户名或密码不能为空");
        }
        
        // 获取用户信息
        User user = userService.getByUsername(loginDTO.getUsername());
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        
        // 验证密码（假设密码已经加密存储）
        if (!PasswordUtil.verify(loginDTO.getPassword(), user.getPassword())) {
            return BaseResponse.error("密码错误");
        }
        
        // 检查用户状态
        if (!"1".equals(user.getStatus())) {
            return BaseResponse.error("账号已被禁用");
        }
        
        // 执行登录
        StpUtil.login(user.getId());
        
        // 返回token
        return BaseResponse.success(StpUtil.getTokenValue());
    }
    
    @PostMapping("/logout")
    public BaseResponse<Void> logout() {
        StpUtil.logout();
        return BaseResponse.success();
    }
    

    @GetMapping("/info")
    public BaseResponse<UserInfoVO> getUserInfo() {
        // 获取当前登录用户ID
        Long userId = StpUtil.getLoginIdAsLong();
        User user = userService.getById(userId);
        if (user == null) {
            return BaseResponse.fail("用户不存在");
        }
        
        UserInfoVO userInfo = new UserInfoVO();
        BeanUtil.copyProperties(user, userInfo);
        return BaseResponse.success(userInfo);
    }

    

}
