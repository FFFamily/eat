package com.tutu.api.config.permission;

import cn.dev33.satoken.stp.StpInterface;

import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.service.AdPermissionService;
import com.tutu.admin_user.service.AdRoleService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component
public class PermissionCheckConfig implements StpInterface {
    @Resource
    private AdRoleService adRoleService;
    @Resource
    private AdPermissionService adPermissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        String userId = String.valueOf(loginId);
        // SUPER_ADMIN: fixed userId=1 or role code SUPER_ADMIN is handled in service.
        return adPermissionService.findByUserId(userId).stream().map(AdPermission::getCode).toList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String userId = String.valueOf(loginId);
        return adRoleService.findByUserId(userId).stream().map(AdRole::getCode).toList();
    }
}
