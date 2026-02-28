package com.tutu.api.config.permission;

import cn.dev33.satoken.stp.StpInterface;

import com.tutu.common.constant.AdminConstant;
import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.service.AdPermissionService;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.common.constant.CommonConstant;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
        // 超级管理员：固定 userId=1 默认具备全部权限（兼容历史库的“内置管理员账号”）
        if (AdminConstant.ADMIN_ID.equals(userId)) {
            return adPermissionService.list(new LambdaQueryWrapper<AdPermission>()
                            .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                            .eq(AdPermission::getStatus, 1))
                    .stream().map(AdPermission::getCode).toList();
        }

        // 按角色码判断管理员（推荐：用角色码而不是固定ID）
        List<AdRole> roles = adRoleService.findByUserId(userId);
        boolean isAdmin = roles.stream().anyMatch(r -> {
            String code = r.getCode();
            return "ADMIN".equalsIgnoreCase(code) || "SUPER_ADMIN".equalsIgnoreCase(code) || "admin".equalsIgnoreCase(code);
        });
        if (isAdmin) {
            return adPermissionService.list(new LambdaQueryWrapper<AdPermission>()
                            .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                            .eq(AdPermission::getStatus, 1))
                    .stream().map(AdPermission::getCode).toList();
        }

        return adPermissionService.findByUserId(userId).stream().map(AdPermission::getCode).toList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        String userId = String.valueOf(loginId);
        return adRoleService.findByUserId(userId).stream().map(AdRole::getCode).toList();
    }
}
