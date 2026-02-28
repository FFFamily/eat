package com.tutu.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 审计日志注解：用于记录关键后台操作（用户/角色/权限等）
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditLog {
    /**
     * 动作标识（如 user.create / role.bind_permissions）
     */
    String action();

    /**
     * 目标类型（如 user / role / permission）
     */
    String targetType() default "";

    /**
     * 是否记录请求参数（可能包含敏感信息时应关闭）
     */
    boolean recordParams() default true;
}

