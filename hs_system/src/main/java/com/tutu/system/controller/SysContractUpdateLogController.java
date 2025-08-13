package com.tutu.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.system.entity.SysContractLog;
import com.tutu.system.service.SysContractLogService;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;

import jakarta.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同更新日志Controller
 */
@RestController
@RequestMapping("/system/contract/log")
public class SysContractUpdateLogController {

    @Resource
    private SysContractLogService sysContractLogService;
    @Resource
    private AdUserService adUserService;

    /**
     * 根据合同ID查询更新日志
     */
    @GetMapping("/list/{contractId}")
    public BaseResponse<List<SysContractLog>> getByContractId(@PathVariable String contractId) {
        LambdaQueryWrapper<SysContractLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractLog::getContractId, contractId);
        List<SysContractLog> list = sysContractLogService.list(queryWrapper);
        List<String> userIds = list.stream().map(SysContractLog::getOperatorId).collect(Collectors.toList());
        HashMap<String, AdUser> userMap = adUserService.getUserMapById(userIds);
        list.forEach(log -> {
            log.setOperatorName(userMap.get(log.getOperatorId()).getNickname());
        });
        return BaseResponse.success(list);
    }

    /**
     * 分页查询合同更新日志
     */
    @GetMapping("/page")
    public BaseResponse<IPage<SysContractLog>> getPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysContractLog> page = new Page<>(pageNum, pageSize);
        IPage<SysContractLog> result = sysContractLogService.page(page);
        return BaseResponse.success(result);
    }
} 