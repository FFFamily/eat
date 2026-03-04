package com.tutu.common.exceptions;

/**
 * 403 - Forbidden（业务自定义，无需走 Sa-Token 的 NotPermissionException）
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}

