package com.tutu.common.exceptions;

/**
 * 服务异常
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
}
