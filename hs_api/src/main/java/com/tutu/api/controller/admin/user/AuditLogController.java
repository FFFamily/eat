package com.tutu.api.controller.admin.user;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.SysAuditLog;
import com.tutu.admin_user.service.SysAuditLogService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.PermissionRequired;
import jakarta.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/admin/audit-log")
public class AuditLogController {

    @Resource
    private SysAuditLogService sysAuditLogService;

    @PermissionRequired("audit:list")
    @GetMapping("/page")
    public BaseResponse<IPage<SysAuditLog>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String actorUserId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String targetType,
            @RequestParam(required = false) String targetId,
            @RequestParam(required = false) Integer result,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime
    ) {
        Page<SysAuditLog> page = new Page<>(current, size);
        LambdaQueryWrapper<SysAuditLog> qw = new LambdaQueryWrapper<>();
        qw.eq(StrUtil.isNotBlank(actorUserId), SysAuditLog::getActorUserId, actorUserId);
        qw.like(StrUtil.isNotBlank(action), SysAuditLog::getAction, action);
        qw.eq(StrUtil.isNotBlank(targetType), SysAuditLog::getTargetType, targetType);
        qw.eq(StrUtil.isNotBlank(targetId), SysAuditLog::getTargetId, targetId);
        qw.eq(result != null, SysAuditLog::getResult, result);
        qw.ge(startTime != null, SysAuditLog::getCreateTime, startTime);
        qw.le(endTime != null, SysAuditLog::getCreateTime, endTime);
        qw.orderByDesc(SysAuditLog::getCreateTime);
        return BaseResponse.success(sysAuditLogService.page(page, qw));
    }
}

