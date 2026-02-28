package com.tutu.api.controller.admin.user;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.SysLoginLog;
import com.tutu.admin_user.service.SysLoginLogService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.PermissionRequired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping("/admin/login-log")
public class LoginLogController {

    @Resource
    private SysLoginLogService sysLoginLogService;

    @PermissionRequired("audit:list")
    @GetMapping("/page")
    public BaseResponse<IPage<SysLoginLog>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer success,
            @RequestParam(required = false) String ip,
            @RequestParam(required = false) String loginType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime
    ) {
        Page<SysLoginLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysLoginLog> qw = new LambdaQueryWrapper<>();
        qw.like(StrUtil.isNotBlank(username), SysLoginLog::getUsername, username);
        qw.eq(success != null, SysLoginLog::getSuccess, success);
        qw.like(StrUtil.isNotBlank(ip), SysLoginLog::getIp, ip);
        qw.eq(StrUtil.isNotBlank(loginType), SysLoginLog::getLoginType, loginType);
        qw.ge(startTime != null, SysLoginLog::getCreateTime, startTime);
        qw.le(endTime != null, SysLoginLog::getCreateTime, endTime);
        qw.orderByDesc(SysLoginLog::getCreateTime);
        return BaseResponse.success(sysLoginLogService.page(page, qw));
    }
}
