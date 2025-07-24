package com.tutu.api.controller.mobile;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.tutu.api.service.LoginService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.User;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.user.request.LoginRequest;
import com.tutu.user.response.LoginUserResponse;
import com.tutu.user.service.RoleService;
import com.tutu.user.service.UserService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/wx/auth")
public class UserLoginController {
    @Resource
    private UserService userService;
    @Resource
    private RoleService roleService;
    @Resource
    private LoginService loginService;

    /**
     * 登录
     * @param loginRequest 登录请求
     * @return  登录结果
     */
    @PostMapping("/login")
    public BaseResponse<String> login(@RequestBody @Valid LoginRequest loginRequest) {
        // 获取用户信息
        User user = userService.getUserByUsername(loginRequest.getUsername());
        loginService.doLogin(user,loginRequest.getPassword());
        // 登录
        StpUtil.login(user.getId());
        // 返回token
        String tokenValue = StpUtil.getTokenValue();
        return BaseResponse.success(tokenValue);
    }
    /**
     * 登出
     */
    @GetMapping("/logout")
    public BaseResponse<Void> logout() {
        StpUtil.logout();
        return BaseResponse.success();
    }
    
    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息
     */
    @GetMapping("/getLoginInfo")
    public BaseResponse<LoginUserResponse> getUserInfo() {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        User user = userService.getById(userId);
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        LoginUserResponse userInfo = new LoginUserResponse();
        BeanUtil.copyProperties(user, userInfo);
        return BaseResponse.success(userInfo);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> register(@RequestBody @Valid LoginRequest loginRequest) {
        // 检查用户名是否已存在
        User existingUser = userService.getUserByUsername(loginRequest.getUsername());
        if (existingUser != null) {
            return BaseResponse.error("用户名已存在");
        }
        User user = new User();
        BeanUtil.copyProperties(loginRequest, user);
        user.setStatus(UserStatusEnum.USE.getCode());
        user.setPassword(SaSecureUtil.md5(loginRequest.getPassword()));
        userService.save(user);
        roleService.firstCreateUserBindRole(user.getId());
        return BaseResponse.success();
    }

}
