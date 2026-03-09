package com.tutu.system.service.entitlement;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.tenant.TenantContext;
import com.tutu.system.entity.entitlement.SysTenantPackage;
import com.tutu.system.mapper.entitlement.SysTenantPackageMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 平台给租户开通/续费套餐、直开模块的写入逻辑。
 *
 * 注意：tenant-scoped 表必须在目标 tenantId 的 TenantContext 下执行（TenantLine）。
 */
@Service
public class SysTenantSubscriptionService {

    @Resource
    private SysTenantPackageMapper sysTenantPackageMapper;
    @Resource
    private TenantEntitlementService tenantEntitlementService;

    public List<SysTenantPackage> listTenantPackages(String tenantId) {
        return runInTenant(tenantId, () -> {
            LambdaQueryWrapper<SysTenantPackage> w = new LambdaQueryWrapper<>();
            w.eq(SysTenantPackage::getIsDeleted, CommonConstant.NO_STR)
                    .orderByDesc(SysTenantPackage::getUpdateTime);
            return sysTenantPackageMapper.selectList(w);
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void grantPackageSetEndTime(String tenantId, String packageId, Date startTime, Date endTime) {
        if (StrUtil.isBlank(tenantId) || StrUtil.isBlank(packageId)) {
            throw new ServiceException("tenantId/packageId不能为空");
        }

        runInTenant(tenantId, () -> {
            Date now = new Date();
            SysTenantPackage row = findTenantPackageRow(packageId);
            if (row == null) {
                row = new SysTenantPackage();
                row.setId(UUID.randomUUID().toString().replace("-", ""));
                row.setPackageId(packageId);
                row.setStatus(CommonConstant.YES_INT);
                row.setStartTime(startTime == null ? now : startTime);
                row.setEndTime(endTime);
                row.setIsDeleted(CommonConstant.NO_STR);
                sysTenantPackageMapper.insert(row);
            } else {
                row.setStatus(CommonConstant.YES_INT);
                if (row.getStartTime() == null) row.setStartTime(startTime == null ? now : startTime);
                if (startTime != null) row.setStartTime(startTime);
                row.setEndTime(endTime);
                sysTenantPackageMapper.updateById(row);
            }
            return null;
        });
        tenantEntitlementService.evictTenant(tenantId);
    }

    @Transactional(rollbackFor = Exception.class)
    public void changePackageStatus(String tenantId, String packageId, Integer status) {
        if (status == null) throw new ServiceException("status不能为空");
        runInTenant(tenantId, () -> {
            SysTenantPackage row = findTenantPackageRow(packageId);
            if (row == null) return null;
            row.setStatus(status);
            sysTenantPackageMapper.updateById(row);
            return null;
        });
        tenantEntitlementService.evictTenant(tenantId);
    }

    private SysTenantPackage findTenantPackageRow(String packageId) {
        LambdaQueryWrapper<SysTenantPackage> w = new LambdaQueryWrapper<>();
        w.eq(SysTenantPackage::getPackageId, packageId)
                .eq(SysTenantPackage::getIsDeleted, CommonConstant.NO_STR)
                .last("limit 1");
        return sysTenantPackageMapper.selectOne(w);
    }

    private <T> T runInTenant(String tenantId, java.util.function.Supplier<T> fn) {
        if (StrUtil.isBlank(tenantId)) throw new ServiceException("tenantId不能为空");
        String current = TenantContext.getTenantId();
        if (StrUtil.equals(current, tenantId)) {
            return fn.get();
        }
        String oldTid = TenantContext.getTenantId();
        String oldCode = TenantContext.getTenantCode();
        boolean oldIgnore = TenantContext.isIgnoreTenantLine();
        try {
            TenantContext.setTenantId(tenantId);
            // For tenant-scoped tables, ensure TenantLine is enabled inside runInTenant.
            TenantContext.setIgnoreTenantLine(false);
            return fn.get();
        } finally {
            TenantContext.clear();
            if (StrUtil.isNotBlank(oldTid)) TenantContext.setTenantId(oldTid);
            if (StrUtil.isNotBlank(oldCode)) TenantContext.setTenantCode(oldCode);
            if (oldIgnore) TenantContext.setIgnoreTenantLine(true);
        }
    }
}
