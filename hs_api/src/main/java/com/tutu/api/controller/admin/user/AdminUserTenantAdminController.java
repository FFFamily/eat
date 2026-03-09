package com.tutu.api.controller.admin.user;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.admin_user.entity.AdUserTenant;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.admin_user.service.AdUserTenantService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.SysTenant;
import com.tutu.system.service.SysTenantService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 平台：管理用户加入哪些租户（成员关系）。
 *
 * <p>注意：该能力为平台级运维能力，默认仅 platform admin(userId=1) 可访问。</p>
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserTenantAdminController {

    @Resource
    private AdUserService adUserService;
    @Resource
    private AdUserTenantService adUserTenantService;
    @Resource
    private SysTenantService sysTenantService;

    private void checkPlatformAdmin() {
        StpUtil.checkLogin();
        if (!AdminConstant.ADMIN_ID.equals(StpUtil.getLoginIdAsString())) {
            throw new ServiceException("仅平台管理员可操作");
        }
    }

    public record TenantIdsReq(@NotEmpty List<String> tenantIds) {
    }

    /**
     * 查询用户已加入的租户ID列表（仅包含未删除的成员关系）。
     */
    @GetMapping("/{userId}/tenants")
    public BaseResponse<List<String>> listUserTenants(@PathVariable String userId) {
        checkPlatformAdmin();
        if (StrUtil.isBlank(userId) || adUserService.getById(userId) == null) {
            throw new ServiceException("用户不存在");
        }
        List<AdUserTenant> links = adUserTenantService.listByUserId(userId);
        List<String> ids = links.stream()
                .map(AdUserTenant::getTenantId)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .toList();
        return BaseResponse.success(ids);
    }

    /**
     * 将用户加入指定租户（幂等）。不会移除用户已有的其他租户。
     */
    @PostMapping("/{userId}/tenants")
    public BaseResponse<Void> addUserTenants(@PathVariable String userId, @Valid @RequestBody TenantIdsReq req) {
        checkPlatformAdmin();
        if (StrUtil.isBlank(userId) || adUserService.getById(userId) == null) {
            throw new ServiceException("用户不存在");
        }
        List<String> tenantIds = (req == null ? null : req.tenantIds());
        if (tenantIds == null || tenantIds.isEmpty()) {
            throw new ServiceException("tenantIds不能为空");
        }
        Set<String> toAdd = tenantIds.stream().filter(StrUtil::isNotBlank).map(String::trim).collect(Collectors.toSet());
        if (toAdd.isEmpty()) throw new ServiceException("tenantIds不能为空");

        // validate tenants exist (non-deleted). status can be 0/1; disabled tenants will not be selectable on login anyway.
        LambdaQueryWrapper<SysTenant> tw = new LambdaQueryWrapper<>();
        tw.in(SysTenant::getId, toAdd)
                .eq(SysTenant::getIsDeleted, CommonConstant.NO_STR);
        List<SysTenant> tenants = sysTenantService.list(tw);
        Set<String> exists = tenants.stream().map(SysTenant::getId).collect(Collectors.toSet());
        if (exists.size() != toAdd.size()) {
            toAdd.removeAll(exists);
            throw new ServiceException("租户不存在: " + String.join(",", toAdd));
        }

        for (String tid : exists) {
            AdUserTenant link = adUserTenantService.getByUserIdAndTenantId(userId, tid);
            if (link == null) {
                link = new AdUserTenant();
                link.setUserId(userId);
                link.setTenantId(tid);
                link.setStatus(CommonConstant.YES_INT);
                link.setIsDeleted(CommonConstant.NO_STR);
                adUserTenantService.save(link);
            } else {
                if (link.getStatus() == null || link.getStatus() == 0) {
                    link.setStatus(CommonConstant.YES_INT);
                }
                if (!CommonConstant.NO_STR.equals(link.getIsDeleted())) {
                    link.setIsDeleted(CommonConstant.NO_STR);
                }
                adUserTenantService.updateById(link);
            }
        }

        return BaseResponse.success();
    }

    /**
     * 设置用户所属租户（精确覆盖）。
     *
     * <p>注意：tenantIds 不能为空；如果置空会导致用户无法登录。</p>
     */
    @PutMapping("/{userId}/tenants")
    public BaseResponse<Void> setUserTenants(@PathVariable String userId, @Valid @RequestBody TenantIdsReq req) {
        checkPlatformAdmin();
        if (StrUtil.isBlank(userId) || adUserService.getById(userId) == null) {
            throw new ServiceException("用户不存在");
        }

        List<String> tenantIds = (req == null ? null : req.tenantIds());
        if (tenantIds == null || tenantIds.isEmpty()) {
            throw new ServiceException("tenantIds不能为空");
        }
        Set<String> desired = tenantIds.stream().filter(StrUtil::isNotBlank).map(String::trim).collect(Collectors.toSet());
        if (desired.isEmpty()) throw new ServiceException("tenantIds不能为空");

        // validate tenants exist
        LambdaQueryWrapper<SysTenant> tw = new LambdaQueryWrapper<>();
        tw.in(SysTenant::getId, desired)
                .eq(SysTenant::getIsDeleted, CommonConstant.NO_STR);
        List<SysTenant> tenants = sysTenantService.list(tw);
        Set<String> exists = tenants.stream().map(SysTenant::getId).collect(Collectors.toSet());
        if (exists.size() != desired.size()) {
            desired.removeAll(exists);
            throw new ServiceException("租户不存在: " + String.join(",", desired));
        }

        // upsert desired links
        for (String tid : exists) {
            AdUserTenant link = adUserTenantService.getByUserIdAndTenantId(userId, tid);
            if (link == null) {
                link = new AdUserTenant();
                link.setUserId(userId);
                link.setTenantId(tid);
                link.setStatus(CommonConstant.YES_INT);
                link.setIsDeleted(CommonConstant.NO_STR);
                adUserTenantService.save(link);
            } else {
                link.setStatus(CommonConstant.YES_INT);
                link.setIsDeleted(CommonConstant.NO_STR);
                adUserTenantService.updateById(link);
            }
        }

        // remove other links (soft delete)
        LambdaQueryWrapper<AdUserTenant> lw = new LambdaQueryWrapper<>();
        lw.eq(AdUserTenant::getUserId, userId)
                .eq(AdUserTenant::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserTenant> links = adUserTenantService.list(lw);
        for (AdUserTenant l : links) {
            if (l == null) continue;
            if (!exists.contains(l.getTenantId())) {
                l.setIsDeleted(CommonConstant.YES_STR);
                adUserTenantService.updateById(l);
            }
        }

        return BaseResponse.success();
    }

    /**
     * 将用户从指定租户移除（软删成员关系）。
     */
    @DeleteMapping("/{userId}/tenant/{tenantId}")
    public BaseResponse<Void> removeUserTenant(@PathVariable String userId, @PathVariable String tenantId) {
        checkPlatformAdmin();
        if (StrUtil.isBlank(userId) || adUserService.getById(userId) == null) {
            throw new ServiceException("用户不存在");
        }
        if (StrUtil.isBlank(tenantId)) throw new ServiceException("tenantId不能为空");
        AdUserTenant link = adUserTenantService.getByUserIdAndTenantId(userId, tenantId);
        if (link != null) {
            link.setIsDeleted(CommonConstant.YES_STR);
            adUserTenantService.updateById(link);
        }
        return BaseResponse.success();
    }
}
