package com.tutu.api.config.aspect;

import cn.dev33.satoken.stp.StpUtil;
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
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        PermissionRequired permissionRequired = method.getAnnotation(PermissionRequired.class);
        String[] requiredPermission = permissionRequired.value();
        StpUtil.checkPermissionAnd(requiredPermission);
    }
}
