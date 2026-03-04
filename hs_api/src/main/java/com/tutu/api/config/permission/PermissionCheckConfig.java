package com.tutu.api.config.permission;

import cn.dev33.satoken.stp.StpInterface;

import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.service.AdPermissionService;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.RoleConstant;
import com.tutu.common.tenant.TenantConstants;
import com.tutu.common.tenant.TenantContext;
import com.tutu.system.service.entitlement.TenantEntitlementService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;

import java.util.List;
import java.util.Set;

/**
 * 自定义权限加载接口实现类
 */
@Component
public class PermissionCheckConfig implements StpInterface {
    @Resource
    private AdRoleService adRoleService;
    @Resource
    private AdPermissionService adPermissionService;
    @Resource
    private TenantEntitlementService tenantEntitlementService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String userId = String.valueOf(loginId);
        // 平台管理员：不受租户套餐限制，直接返回全量权限码
        if (AdminConstant.ADMIN_ID.equals(userId)) {
            return adPermissionService.listAllEnabled().stream().map(AdPermission::getCode).toList();
        }

        String tenantId = resolveTenantId();
        Set<String> allowedPermissionIds = tenantEntitlementService.getAllowedPermissionIds(tenantId);
        if (allowedPermissionIds == null || allowedPermissionIds.isEmpty()) {
            return List.of();
        }

        boolean isSuperAdmin = adRoleService.findByUserId(userId).stream().anyMatch(r ->
                r != null && StrUtil.isNotBlank(r.getCode()) && RoleConstant.SUPER_ADMIN.equalsIgnoreCase(r.getCode())
        );

        // 租户 SUPER_ADMIN：拥有“该租户已开通的全部权限点”（不再返回全库 ad_permission）
        if (isSuperAdmin) {
            return adPermissionService.listByIdsEnabled(allowedPermissionIds).stream().map(AdPermission::getCode).toList();
        }

        // 普通用户：角色权限 ∩ 租户可用上限
        return adPermissionService.findByUserId(userId).stream()
                .filter(p -> p != null && allowedPermissionIds.contains(p.getId()))
                .map(AdPermission::getCode)
                .toList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String userId = String.valueOf(loginId);
        return adRoleService.findByUserId(userId).stream().map(AdRole::getCode).toList();
    }

    private String resolveTenantId() {
        // Prefer token session (authoritative after login)
        if (StpUtil.isLogin()) {
            Object v = StpUtil.getTokenSession().get(TenantConstants.SESSION_TENANT_ID);
            String tid = v == null ? null : String.valueOf(v);
            if (StrUtil.isNotBlank(tid)) return tid;
        }
        // Fallback to request-scoped ThreadLocal
        return TenantContext.getRequiredTenantId();
    }
}
