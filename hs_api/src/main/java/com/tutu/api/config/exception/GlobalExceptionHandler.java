package com.tutu.api.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.hutool.http.HttpStatus;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    public BaseResponse<Void> handleException(Exception ex){
        logError("Exception", ex);
        return BaseResponse.error("服务器异常: " + ex.getMessage());
    }

    @ExceptionHandler(value=Throwable.class)
    public BaseResponse<Void> handleException(Throwable ex){
        logError("Throwable", ex);
        return BaseResponse.error(ex.getMessage());
    }

    @ExceptionHandler(value= ServiceException.class)
    public BaseResponse<Void> handleException(ServiceException ex){
        logError("ServiceException", ex);
        return BaseResponse.error("服务器内部错误: "+ex.getMessage());
    }

    @ExceptionHandler(value= NotLoginException.class)
    public BaseResponse<Void> handleException(NotLoginException ex){
        logError("NotLoginException", ex);
        return BaseResponse.error(HttpStatus.HTTP_UNAUTHORIZED,"当前登录已过期，请重新登陆: "+ex.getMessage());
    }

    @ExceptionHandler(value= NotPermissionException.class)
    public BaseResponse<Void> handleException(NotPermissionException ex){
        logError("NotPermissionException", ex);
        return BaseResponse.error(HttpStatus.HTTP_FORBIDDEN,"无权访问");
    }

    private void logError(String exceptionType, Throwable ex) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                log.error("\n========== 异常信息 ==========\n" +
                        "异常类型: {}\n" +
                        "请求URL: {}\n" +
                        "请求方法: {}\n" +
                        "Controller: {}\n" +
                        "异常信息: {}\n" +
                        "========== 异常堆栈 ==========",
                        exceptionType,
                        request.getRequestURL(),
                        request.getMethod(),
                        request.getServletPath(),
                        ex.getMessage(),
                        ex);
            } else {
                log.error("异常类型: {}, 异常信息: {}", exceptionType, ex.getMessage(), ex);
            }
        } catch (Exception e) {
            log.error("异常类型: {}, 异常信息: {}", exceptionType, ex.getMessage(), ex);
        }
    }
}
