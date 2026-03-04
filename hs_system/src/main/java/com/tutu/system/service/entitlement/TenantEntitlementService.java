package com.tutu.system.service.entitlement;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.tenant.TenantContext;
import com.tutu.system.dto.entitlement.EntitlementSnapshot;
import com.tutu.system.entity.entitlement.*;
import com.tutu.system.mapper.entitlement.*;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * 租户授权(Entitlement)计算：
 * - 租户已开通模块 = 直开模块 ∪ (开通套餐 -> 套餐包含模块)
 * - 租户可用权限上限 = 上述模块绑定的权限点并集
 *
 * 说明：由于租户订阅表启用 TenantLine，需要确保 TenantContext.tenantId 与目标租户一致；
 *      平台跨租户操作场景需通过 runInTenant(...) 强制设置上下文。
 */
@Service
public class TenantEntitlementService {

    private static final long CACHE_TTL_MS = 60_000L;

    private static final class CacheItem {
        final EntitlementSnapshot snapshot;
        final long loadedAtMs;

        CacheItem(EntitlementSnapshot snapshot, long loadedAtMs) {
            this.snapshot = snapshot;
            this.loadedAtMs = loadedAtMs;
        }
    }

    private final ConcurrentHashMap<String, CacheItem> cache = new ConcurrentHashMap<>();

    @Resource
    private SysPackagePermissionMapper sysPackagePermissionMapper;
    @Resource
    private SysTenantPackageMapper sysTenantPackageMapper;

    public void evictTenant(String tenantId) {
        if (StrUtil.isBlank(tenantId)) return;
        cache.remove(tenantId);
    }

    public void evictAll() {
        cache.clear();
    }

    public EntitlementSnapshot getSnapshot(String tenantId) {
        if (StrUtil.isBlank(tenantId)) {
            throw new ServiceException("tenantId不能为空");
        }
        long now = System.currentTimeMillis();
        CacheItem hit = cache.get(tenantId);
        if (hit != null && (now - hit.loadedAtMs) < CACHE_TTL_MS) {
            return hit.snapshot;
        }
        EntitlementSnapshot loaded = loadSnapshot(tenantId, new Date());
        cache.put(tenantId, new CacheItem(loaded, now));
        return loaded;
    }

    public Set<String> getAllowedPermissionIds(String tenantId) {
        return getSnapshot(tenantId).getAllowedPermissionIds();
    }

    public boolean isModuleActive(String tenantId, String moduleId) {
        // package-only model：不再提供模块维度
        return false;
    }

    private EntitlementSnapshot loadSnapshot(String tenantId, Date now) {
        return runInTenant(tenantId, () -> doLoadSnapshot(tenantId, now));
    }

    private EntitlementSnapshot doLoadSnapshot(String tenantId, Date now) {
        // 1) Active packages
        LambdaQueryWrapper<SysTenantPackage> pw = new LambdaQueryWrapper<>();
        pw.eq(SysTenantPackage::getIsDeleted, CommonConstant.NO_STR)
                .eq(SysTenantPackage::getStatus, CommonConstant.YES_INT)
                .and(w -> w.isNull(SysTenantPackage::getStartTime).or().le(SysTenantPackage::getStartTime, now))
                .and(w -> w.isNull(SysTenantPackage::getEndTime).or().ge(SysTenantPackage::getEndTime, now));
        List<SysTenantPackage> packages = sysTenantPackageMapper.selectList(pw);
        Set<String> packageIds = packages.stream().map(SysTenantPackage::getPackageId).filter(StrUtil::isNotBlank).collect(Collectors.toSet());

        // 2) package -> permissions
        Set<String> allowedPermissionIds = new HashSet<>();
        if (!packageIds.isEmpty()) {
            LambdaQueryWrapper<SysPackagePermission> w = new LambdaQueryWrapper<>();
            w.in(SysPackagePermission::getPackageId, packageIds)
                    .eq(SysPackagePermission::getIsDeleted, CommonConstant.NO_STR);
            List<SysPackagePermission> pps = sysPackagePermissionMapper.selectList(w);
            for (SysPackagePermission pp : pps) {
                if (StrUtil.isNotBlank(pp.getPermissionId())) {
                    allowedPermissionIds.add(pp.getPermissionId());
                }
            }
        }

        EntitlementSnapshot snap = new EntitlementSnapshot();
        snap.setTenantId(tenantId);
        snap.setLoadedAt(now);
        snap.setActiveModuleIds(Collections.emptySet());
        snap.setActiveModuleCodes(Collections.emptySet());
        snap.setAllowedPermissionIds(Collections.unmodifiableSet(allowedPermissionIds));
        return snap;
    }

    private <T> T runInTenant(String tenantId, Supplier<T> fn) {
        String current = TenantContext.getTenantId();
        if (StrUtil.equals(current, tenantId)) {
            return fn.get();
        }
        String oldTid = TenantContext.getTenantId();
        String oldCode = TenantContext.getTenantCode();
        try {
            TenantContext.setTenantId(tenantId);
            return fn.get();
        } finally {
            TenantContext.clear();
            if (StrUtil.isNotBlank(oldTid)) TenantContext.setTenantId(oldTid);
            if (StrUtil.isNotBlank(oldCode)) TenantContext.setTenantCode(oldCode);
        }
    }
}
