package com.tutu.api.controller.wx;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.api.service.LoginService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.user.entity.Account;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.user.request.LoginRequest;
import com.tutu.user.response.LoginUserResponse;
import com.tutu.user.service.RoleService;
import com.tutu.user.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/wx/auth")
public class WxLoginController {
    @Resource
    private AccountService accountService;
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
        Account account = accountService.getUserByUsername(loginRequest.getUsername());
        loginService.doLogin(account,loginRequest.getPassword());
        // 登录
        StpUtil.login(account.getId());
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
        Account account = accountService.getById(userId);
        if (account == null) {
            return BaseResponse.error("用户不存在");
        }
        LoginUserResponse userInfo = new LoginUserResponse();
        BeanUtil.copyProperties(account, userInfo);
        return BaseResponse.success(userInfo);
    }

    /**
     * 注册
     */
    @PostMapping("/register")
    @Transactional(rollbackFor = Exception.class)
    public BaseResponse<Void> register(@RequestBody @Valid Account loginRequest) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<Account> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getUsername, loginRequest.getUsername());
        Account existingAccount = accountService.getOne(queryWrapper);
        if (existingAccount != null) {
            return BaseResponse.error("用户名已存在");
        }
        // 校验手机号
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Account::getPhone, loginRequest.getPhone());
        existingAccount = accountService.getOne(queryWrapper);
        if (existingAccount != null) {
            return BaseResponse.error("手机号已存在");
        }
        // 校验身份证
        queryWrapper = new LambdaQueryWrapper<>();
        Account account = new Account();
        BeanUtil.copyProperties(loginRequest, account);
        account.setStatus(UserStatusEnum.USE.getCode());
        account.setPassword(SaSecureUtil.md5(loginRequest.getPassword()));
        accountService.save(account);
        return BaseResponse.success();
    }

}
