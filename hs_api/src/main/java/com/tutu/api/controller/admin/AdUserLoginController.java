package com.tutu.api.controller.admin;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.api.service.LoginService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.user.request.LoginRequest;
import com.tutu.user.response.LoginUserResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/ad/auth")
public class AdUserLoginController {
    @Resource
    private AdUserService adUserService;
    @Resource
    private AdRoleService adRoleService;
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
        AdUser user = adUserService.findByUsername(loginRequest.getUsername());
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
        // 使用 AdUserService 获取用户信息
        AdUser user = adUserService.getById(userId);
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
        // 检查用户名是否已存
        AdUser existingUser = adUserService.findByUsername(loginRequest.getUsername());
        if (existingUser != null) {
            return BaseResponse.error("用户已存在");
        }
        AdUser user = new AdUser();
        BeanUtil.copyProperties(loginRequest, user);
        user.setStatus(UserStatusEnum.USE.getCode());
        user.setPassword(SaSecureUtil.md5(loginRequest.getPassword()));
        adUserService.save(user);
        adRoleService.firstCreateUserBindRole(user.getId());
        return BaseResponse.success();
    }

}
