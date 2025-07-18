package com.tutu.common.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.tutu.common.annotation.OperationLog;
import com.tutu.common.enums.OperationType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 操作日志切面
 */
@Slf4j
@Aspect
@Component
public class OperationLogAspect {
    
    @Autowired(required = false)
    private OperationLogService operationLogService;
    
    /**
     * 定义切点
     */
    @Pointcut("@annotation(com.tutu.common.annotation.OperationLog)")
    public void operationLogPointcut() {}
    
    /**
     * 环绕通知
     */
    @Around("operationLogPointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;
        
        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 记录操作日志
            recordOperationLog(joinPoint, result, exception, executionTime);
        }
    }
    
    /**
     * 异常通知
     */
    @AfterThrowing(pointcut = "operationLogPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        log.error("操作异常: {}", e.getMessage(), e);
    }
    
    /**
     * 记录操作日志
     */
    private void recordOperationLog(ProceedingJoinPoint joinPoint, Object result, Exception exception, long executionTime) {
        try {
            // 如果没有注入OperationLogService，则跳过记录
            if (operationLogService == null) {
                return;
            }
            
            // 获取注解信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            OperationLog operationLogAnnotation = method.getAnnotation(OperationLog.class);
            
            if (operationLogAnnotation == null) {
                return;
            }
            
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
            
            // 创建操作日志对象
            com.tutu.admin_user.entity.OperationLog operationLog = new com.tutu.admin_user.entity.OperationLog();
            
            // 设置基本信息
            operationLog.setOperationType(operationLogAnnotation.type().getCode());
            operationLog.setOperationName(StrUtil.isNotBlank(operationLogAnnotation.value()) ? 
                    operationLogAnnotation.value() : method.getName());
            operationLog.setMethodName(method.getDeclaringClass().getName() + "." + method.getName());
            operationLog.setExecutionTime(executionTime);
            operationLog.setStatus(exception == null ? 1 : 0);
            operationLog.setCreateTime(new Date());
            operationLog.setUpdateTime(new Date());
            
            // 设置用户信息
            try {
                if (StpUtil.isLogin()) {
                    String userId = StpUtil.getLoginIdAsString();
                    operationLog.setUserId(userId);
                    operationLog.setCreateBy(userId);
                    operationLog.setUpdateBy(userId);
                    // 这里可以根据userId查询用户名，暂时使用userId
                    operationLog.setUsername(userId);
                }
            } catch (Exception e) {
                log.warn("获取用户信息失败: {}", e.getMessage());
            }
            
            // 设置请求信息
            if (request != null) {
                operationLog.setRequestMethod(request.getMethod());
                operationLog.setRequestUrl(request.getRequestURL().toString());
                operationLog.setIpAddress(getIpAddress(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
                
                // 记录请求参数
                if (operationLogAnnotation.recordParams()) {
                    Object[] args = joinPoint.getArgs();
                    if (args != null && args.length > 0) {
                        try {
                            String params = JSON.toJSONString(args);
                            // 限制参数长度，避免过长
                            if (params.length() > 2000) {
                                params = params.substring(0, 2000) + "...";
                            }
                            operationLog.setRequestParams(params);
                        } catch (Exception e) {
                            operationLog.setRequestParams("参数序列化失败");
                        }
                    }
                }
            }
            
            // 记录响应数据
            if (operationLogAnnotation.recordResponse() && result != null) {
                try {
                    String responseData = JSON.toJSONString(result);
                    // 限制响应数据长度
                    if (responseData.length() > 2000) {
                        responseData = responseData.substring(0, 2000) + "...";
                    }
                    operationLog.setResponseData(responseData);
                } catch (Exception e) {
                    operationLog.setResponseData("响应数据序列化失败");
                }
            }
            
            // 记录异常信息
            if (exception != null && operationLogAnnotation.recordException()) {
                String errorMessage = exception.getMessage();
                if (StrUtil.isNotBlank(errorMessage) && errorMessage.length() > 1000) {
                    errorMessage = errorMessage.substring(0, 1000) + "...";
                }
                operationLog.setErrorMessage(errorMessage);
            }
            
            // 异步记录日志
            operationLogService.recordLog(operationLog);
            
        } catch (Exception e) {
            log.error("记录操作日志失败", e);
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}

/**
 * 操作日志服务接口（用于解决循环依赖）
 */
interface OperationLogService {
    void recordLog(com.tutu.admin_user.entity.OperationLog operationLog);
}
