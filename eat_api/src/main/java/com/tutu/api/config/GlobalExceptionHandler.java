package com.tutu.api.config;

import com.tutu.common.Response.BaseResponse;
import com.tutu.common.exceptions.ServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.rmi.ServerException;

@RestControllerAdvice//标记该类为全局异常处理类
public class GlobalExceptionHandler {
    // 其他异常
    @ExceptionHandler(value=Exception.class)
    public BaseResponse<Void> handleException(Exception ex){
        return BaseResponse.error("服务器内部错误: "+ex.getMessage());
    }

    // 服务手动抛出异常
    @ExceptionHandler(value= ServiceException.class)
    public BaseResponse<Void> handleException(ServiceException ex){
        return BaseResponse.error("服务器内部错误: "+ex.getMessage());
    }


}
