package com.tutu.api.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.hutool.http.HttpStatus;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.exceptions.ServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice//标记该类为全局异常处理类
public class GlobalExceptionHandler {
    // 其他异常
    @ExceptionHandler(value=Exception.class)
    public BaseResponse<Void> handleException(Exception ex){
        ex.printStackTrace();
        return BaseResponse.error("服务器异常");
    }

    // 服务手动抛出异常
    @ExceptionHandler(value= ServiceException.class)
    public BaseResponse<Void> handleException(ServiceException ex){
        return BaseResponse.error("服务器内部错误: "+ex.getMessage());
    }

    @ExceptionHandler(value= NotLoginException.class)
    public BaseResponse<Void> handleException(NotLoginException ex){
        return BaseResponse.error(HttpStatus.HTTP_UNAUTHORIZED,"当前登录已过期，请重新登陆: "+ex.getMessage());
    }

    @ExceptionHandler(value= NotPermissionException.class)
    public BaseResponse<Void> handleException(NotPermissionException ex){
        return BaseResponse.error(HttpStatus.HTTP_FORBIDDEN,"无权访问");
    }

}
