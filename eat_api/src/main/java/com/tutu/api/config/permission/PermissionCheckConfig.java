package com.tutu.api.config.permission;

import cn.dev33.satoken.stp.StpInterface;

import com.tutu.common.constant.AdminConstant;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 */
@Component
public class PermissionCheckConfig implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (AdminConstant.ADMIN_ID.equals(loginId)){
            return List.of("admin.*","user.*");
        }
        return List.of();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return List.of();
    }
}
