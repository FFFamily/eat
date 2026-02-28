package com.tutu.api.config.aspect;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.annotation.PermissionRequired;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class PermissionAspect {
    /**
     * 权限校验
     * @param joinPoint
     */
    @Before("@annotation(com.tutu.common.annotation.PermissionRequired)")
    public void before(JoinPoint joinPoint) {
        // 超级管理员默认放行：避免依赖“权限点 seed 是否完整”才能访问后台能力
        try {
            if (StpUtil.isLogin()) {
                String loginId = StpUtil.getLoginIdAsString();
                if (AdminConstant.ADMIN_ID.equals(loginId)
                        || StpUtil.hasRole("SUPER_ADMIN")
                        || StpUtil.hasRole("ADMIN")) {
                    return;
                }
            }
        } catch (Exception ignore) {
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PermissionRequired permissionRequired = method.getAnnotation(PermissionRequired.class);
        String[] requiredPermission = permissionRequired.value();
        StpUtil.checkPermissionAnd(requiredPermission);
    }
}
