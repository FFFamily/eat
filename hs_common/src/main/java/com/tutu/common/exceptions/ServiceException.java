package com.tutu.common.exceptions;

import com.tutu.common.enums.ServiceExceptionTypeEnum;

/**
 * 服务异常
 */
public class ServiceException extends RuntimeException {
    public ServiceException(String message) {
        super(message);
    }
    public ServiceException(String message, ServiceExceptionTypeEnum typeEnum) {
        super(typeEnum.getTitle()+ message);
    }
}
