package com.tutu.api.config.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.hutool.http.HttpStatus;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.exceptions.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@RestControllerAdvice
@Order(1) // 设置优先级，确保这个处理器优先执行
public class GlobalExceptionHandler {

    // 先处理具体的异常类型，按从具体到抽象的顺序
    @ExceptionHandler(value= ServiceException.class)
    public BaseResponse<Void> handleServiceException(ServiceException ex){
        // 在方法开始处立即输出日志，确认方法被调用
        log.error("【进入ServiceException处理器】异常信息: {}", ex.getMessage());
        System.out.println("【System.out】进入ServiceException处理器，异常信息: " + ex.getMessage());
        
        logError("ServiceException", ex);
        return BaseResponse.error("服务器内部错误: "+ex.getMessage());
    }

    @ExceptionHandler(value= NotLoginException.class)
    public BaseResponse<Void> handleNotLoginException(NotLoginException ex){
        logError("NotLoginException", ex);
        return BaseResponse.error(HttpStatus.HTTP_UNAUTHORIZED,"当前登录已过期，请重新登陆: "+ex.getMessage());
    }

    @ExceptionHandler(value= NotPermissionException.class)
    public BaseResponse<Void> handleNotPermissionException(NotPermissionException ex){
        logError("NotPermissionException", ex);
        return BaseResponse.error(HttpStatus.HTTP_FORBIDDEN,"无权访问");
    }

    // 最后处理通用的异常类型
    @ExceptionHandler(value=Exception.class)
    public BaseResponse<Void> handleException(Exception ex){
        // 在方法开始处立即输出日志，确认方法被调用
        log.error("【进入Exception处理器】异常类型: {}, 异常信息: {}", ex.getClass().getName(), ex.getMessage());
        System.out.println("【System.out】进入Exception处理器，异常类型: " + ex.getClass().getName());
        
        // 如果是ServiceException，不应该被这个处理器捕获，但记录一下
        if (ex instanceof ServiceException) {
            log.error("【警告】ServiceException被Exception处理器捕获了！");
            System.out.println("【System.out】【警告】ServiceException被Exception处理器捕获了！");
        }
        
        logError("Exception", ex);
        return BaseResponse.error("服务器异常: " + ex.getMessage());
    }

    @ExceptionHandler(value=Throwable.class)
    public BaseResponse<Void> handleThrowable(Throwable ex){
        // 在方法开始处立即输出日志，确认方法被调用
        log.error("【进入Throwable处理器】异常类型: {}, 异常信息: {}", ex.getClass().getName(), ex.getMessage());
        System.out.println("【System.out】进入Throwable处理器，异常类型: " + ex.getClass().getName());
        
        logError("Throwable", ex);
        return BaseResponse.error(ex.getMessage());
    }


    private void logError(String exceptionType, Throwable ex) {
        // 在方法开始处立即输出，确认logError被调用
        System.out.println("【System.out】【进入logError方法】异常类型: " + exceptionType);
        log.info("【进入logError方法】异常类型: {}", exceptionType);
        
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String requestUrl = request.getRequestURL() != null ? request.getRequestURL().toString() : "N/A";
                String method = request.getMethod() != null ? request.getMethod() : "N/A";
                String servletPath = request.getServletPath() != null ? request.getServletPath() : "N/A";
                String message = ex.getMessage() != null ? ex.getMessage() : "无异常消息";
                
                System.out.println("【System.out】开始输出详细异常信息");
                log.error("\n========== 异常信息 ==========\n" +
                        "异常类型: {}\n" +
                        "请求URL: {}\n" +
                        "请求方法: {}\n" +
                        "Controller: {}\n" +
                        "异常信息: {}\n" +
                        "========== 异常堆栈 ==========",
                        exceptionType,
                        requestUrl,
                        method,
                        servletPath,
                        message,
                        ex);
            } else {
                String message = ex.getMessage() != null ? ex.getMessage() : "无异常消息";
                System.out.println("【System.out】RequestContextHolder为null，输出简化异常信息");
                log.error("异常类型: {}, 异常信息: {}", exceptionType, message, ex);
            }
        } catch (Exception e) {
            // 如果日志记录本身出错，使用最简单的方式输出
            String message = ex != null && ex.getMessage() != null ? ex.getMessage() : "无异常消息";
            System.err.println("【System.err】日志记录出错: " + e.getMessage());
            System.err.println("【System.err】异常类型: " + exceptionType + ", 异常信息: " + message);
            log.error("异常类型: {}, 异常信息: {} [日志记录出错: {}]", exceptionType, message, e.getMessage(), ex);
        }
    }
}
