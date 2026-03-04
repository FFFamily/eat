package com.tutu.api.service;

import cn.hutool.core.util.StrUtil;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.constant.RoleConstant;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.tenant.TenantContext;
import com.tutu.system.dto.tenant.TenantBootstrapAdminDTO;
import com.tutu.system.entity.SysTenant;
import com.tutu.system.service.SysTenantService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Provision tenant-scoped baseline data (admin role/user) for a new tenant.
 */
@Service
public class TenantProvisionService {

    @Resource
    private SysTenantService sysTenantService;
    @Resource
    private AdRoleService adRoleService;
    @Resource
    private AdUserService adUserService;

    @Transactional(rollbackFor = Exception.class)
    public String bootstrapTenantAdmin(String tenantId, TenantBootstrapAdminDTO dto) {
        if (StrUtil.isBlank(tenantId)) {
            throw new ServiceException("tenantId不能为空");
        }
        SysTenant tenant = sysTenantService.getById(tenantId);
        if (tenant == null) {
            throw new ServiceException("租户不存在: " + tenantId);
        }

        String oldTid = TenantContext.getTenantId();
        String oldCode = TenantContext.getTenantCode();
        try {
            // Force operations inside the target tenant.
            TenantContext.setTenantId(tenant.getId());
            TenantContext.setTenantCode(tenant.getCode());

            AdRole superRole = adRoleService.findByCode(RoleConstant.SUPER_ADMIN);
            if (superRole == null) {
                superRole = new AdRole();
                superRole.setName("超级管理员");
                superRole.setCode(RoleConstant.SUPER_ADMIN);
                superRole.setRemark("内置角色：拥有所有权限");
                superRole.setIsDeleted(CommonConstant.NO_STR);
                superRole.setCreateTime(new Date());
                superRole.setUpdateTime(new Date());
                adRoleService.createRole(superRole);
            }

            AdUser user = new AdUser();
            user.setUsername(dto.getUsername());
            user.setPassword(dto.getPassword());
            user.setNickname(StrUtil.blankToDefault(dto.getNickname(), "租户管理员"));
            user.setPhone(dto.getPhone());
            user.setStatus(UserStatusEnum.USE.getCode());
            adUserService.createUser(user);

            adUserService.assignRoles(user.getId(), List.of(superRole.getId()));
            return user.getId();
        } finally {
            // Restore previous context to avoid leaking to other operations.
            TenantContext.clear();
            if (StrUtil.isNotBlank(oldTid)) {
                TenantContext.setTenantId(oldTid);
            }
            if (StrUtil.isNotBlank(oldCode)) {
                TenantContext.setTenantCode(oldCode);
            }
        }
    }
}

