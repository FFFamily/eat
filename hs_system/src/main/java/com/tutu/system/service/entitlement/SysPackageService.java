package com.tutu.system.service.entitlement;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.entitlement.SysPackage;
import com.tutu.system.entity.entitlement.SysPackagePermission;
import com.tutu.system.mapper.entitlement.SysPackageMapper;
import com.tutu.system.mapper.entitlement.SysPackagePermissionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class SysPackageService extends ServiceImpl<SysPackageMapper, SysPackage> {

    @Resource
    private SysPackagePermissionMapper sysPackagePermissionMapper;

    @Resource
    private TenantEntitlementService tenantEntitlementService;

    public List<String> listBoundPermissionIds(String packageId) {
        if (StrUtil.isBlank(packageId)) return List.of();
        LambdaQueryWrapper<SysPackagePermission> w = new LambdaQueryWrapper<>();
        w.eq(SysPackagePermission::getPackageId, packageId)
                .eq(SysPackagePermission::getIsDeleted, CommonConstant.NO_STR);
        List<SysPackagePermission> list = sysPackagePermissionMapper.selectList(w);
        if (list == null || list.isEmpty()) return List.of();
        return list.stream().map(SysPackagePermission::getPermissionId).filter(StrUtil::isNotBlank).distinct().toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public void setPackagePermissions(String packageId, List<String> permissionIds) {
        if (StrUtil.isBlank(packageId)) {
            throw new ServiceException("packageId不能为空");
        }
        LambdaQueryWrapper<SysPackagePermission> del = new LambdaQueryWrapper<>();
        del.eq(SysPackagePermission::getPackageId, packageId)
                .eq(SysPackagePermission::getIsDeleted, CommonConstant.NO_STR);
        sysPackagePermissionMapper.delete(del);

        if (permissionIds != null && !permissionIds.isEmpty()) {
            Set<String> dedup = new LinkedHashSet<>();
            for (String pid : permissionIds) {
                if (StrUtil.isNotBlank(pid)) dedup.add(pid);
            }
            for (String pid : dedup) {
                SysPackagePermission pp = new SysPackagePermission();
                pp.setId(UUID.randomUUID().toString().replace("-", ""));
                pp.setPackageId(packageId);
                pp.setPermissionId(pid);
                pp.setIsDeleted(CommonConstant.NO_STR);
                sysPackagePermissionMapper.insert(pp);
            }
        }
        // Package-permission mapping changes affect all tenants.
        tenantEntitlementService.evictAll();
    }
}
