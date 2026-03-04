package com.tutu.api.controller.admin.entitlement;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.dto.entitlement.EntitlementSnapshot;
import com.tutu.system.entity.entitlement.SysPackage;
import com.tutu.system.entity.entitlement.SysTenantPackage;
import com.tutu.system.service.entitlement.SysPackageService;
import com.tutu.system.service.entitlement.SysTenantSubscriptionService;
import com.tutu.system.service.entitlement.TenantEntitlementService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 平台：租户订阅配置（购买/续费套餐、直开模块）
 *
 * 注意：该接口路径在 tenant.ignore-paths(/admin/tenant/**) 中，默认不会自动解析 TenantContext；
 *      写 tenant-scoped 表时由服务层 runInTenant(...) 强制切换 tenantId。
 */
@RestController
@RequestMapping("/admin/tenant/{tenantId}/subscription")
public class TenantSubscriptionAdminController {

    @Resource
    private SysTenantSubscriptionService sysTenantSubscriptionService;
    @Resource
    private SysPackageService sysPackageService;
    @Resource
    private TenantEntitlementService tenantEntitlementService;

    private void checkPlatformAdmin() {
        StpUtil.checkLogin();
        if (!AdminConstant.ADMIN_ID.equals(StpUtil.getLoginIdAsString())) {
            throw new ServiceException("仅平台管理员可访问");
        }
    }

    public record GrantPackageReq(
            @NotBlank String packageId,
            Long startTimeMs,
            Long endTimeMs
    ) {
    }

    public record TenantPackageResp(
            String id,
            String packageId,
            String packageCode,
            String packageName,
            Integer status,
            Date startTime,
            Date endTime,
            Long remainDays
    ) {
    }

    @GetMapping("/packages")
    public BaseResponse<List<TenantPackageResp>> listPackages(@PathVariable String tenantId) {
        checkPlatformAdmin();
        List<SysTenantPackage> rows = sysTenantSubscriptionService.listTenantPackages(tenantId);
        if (rows == null || rows.isEmpty()) return BaseResponse.success(List.of());

        Set<String> pkgIds = rows.stream().map(SysTenantPackage::getPackageId).filter(StrUtil::isNotBlank).collect(Collectors.toSet());
        Map<String, SysPackage> pkgMap = pkgIds.isEmpty()
                ? Map.of()
                : sysPackageService.list(new LambdaQueryWrapper<SysPackage>()
                .in(SysPackage::getId, pkgIds)
                .eq(SysPackage::getIsDeleted, CommonConstant.NO_STR)).stream().collect(Collectors.toMap(SysPackage::getId, p -> p, (a, b) -> a));

        Date now = new Date();
        List<TenantPackageResp> out = new ArrayList<>();
        for (SysTenantPackage r : rows) {
            SysPackage p = pkgMap.get(r.getPackageId());
            out.add(new TenantPackageResp(
                    r.getId(),
                    r.getPackageId(),
                    p == null ? null : p.getCode(),
                    p == null ? null : p.getName(),
                    r.getStatus(),
                    r.getStartTime(),
                    r.getEndTime(),
                    remainDays(r.getEndTime(), now)
            ));
        }
        return BaseResponse.success(out);
    }

    @PostMapping("/grant-package")
    public BaseResponse<Void> grantPackage(@PathVariable String tenantId, @Valid @RequestBody GrantPackageReq req) {
        checkPlatformAdmin();
        Date start = req.startTimeMs() == null ? null : new Date(req.startTimeMs());
        Date end = req.endTimeMs() == null ? null : new Date(req.endTimeMs());
        sysTenantSubscriptionService.grantPackageSetEndTime(tenantId, req.packageId(), start, end);
        return BaseResponse.success();
    }

    @PutMapping("/package/{packageId}/status")
    public BaseResponse<Void> changePackageStatus(@PathVariable String tenantId, @PathVariable String packageId, @RequestParam Integer status) {
        checkPlatformAdmin();
        sysTenantSubscriptionService.changePackageStatus(tenantId, packageId, status);
        return BaseResponse.success();
    }

    @GetMapping("/entitlement-snapshot")
    public BaseResponse<EntitlementSnapshot> snapshot(@PathVariable String tenantId) {
        checkPlatformAdmin();
        return BaseResponse.success(tenantEntitlementService.getSnapshot(tenantId));
    }

    private Long remainDays(Date endTime, Date now) {
        if (endTime == null) return null;
        if (now == null) now = new Date();
        long diff = endTime.getTime() - now.getTime();
        if (diff <= 0) return 0L;
        // ceil day
        long dayMs = 24L * 60 * 60 * 1000;
        return (diff + dayMs - 1) / dayMs;
    }
}
