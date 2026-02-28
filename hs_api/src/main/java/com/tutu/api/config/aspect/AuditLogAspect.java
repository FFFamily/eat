package com.tutu.api.config.aspect;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tutu.admin_user.entity.SysAuditLog;
import com.tutu.admin_user.service.SysAuditLogService;
import com.tutu.common.annotation.AuditLog;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * 基础审计日志 AOP：记录后台关键操作到 sys_audit_log。
 *
 * 说明：
 * - 以“不影响主流程”为原则：审计写入异常会被吞掉。
 * - 默认只记录少量字段；如 recordParams=true，会记录参数（会做简单脱敏）。
 */
@Aspect
@Component
public class AuditLogAspect {

    private static final Pattern SECRET_FIELD_PATTERN =
            Pattern.compile("\"(password|oldPassword|newPassword)\"\\s*:\\s*\".*?\"", Pattern.CASE_INSENSITIVE);

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    private SysAuditLogService sysAuditLogService;

    @Around("@annotation(com.tutu.common.annotation.AuditLog)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        AuditLog auditLog = method.getAnnotation(AuditLog.class);

        SysAuditLog log = new SysAuditLog();
        log.setAction(auditLog.action());
        log.setTargetType(auditLog.targetType());
        log.setRequestId(resolveRequestId());

        if (StpUtil.isLogin()) {
            log.setActorUserId(StpUtil.getLoginIdAsString());
        }

        HttpServletRequest request = resolveRequest();
        if (request != null) {
            log.setIp(resolveClientIp(request));
            log.setUa(request.getHeader("User-Agent"));
        }

        if (auditLog.recordParams()) {
            log.setDetailJson(serializeAndMask(signature.getParameterNames(), pjp.getArgs()));
        }

        try {
            Object ret = pjp.proceed();
            log.setResult(1);
            return ret;
        } catch (Throwable ex) {
            log.setResult(0);
            String msg = ex.getMessage();
            if (msg != null && msg.length() > 255) {
                msg = msg.substring(0, 255);
            }
            log.setErrorMessage(msg);
            throw ex;
        } finally {
            if (StrUtil.isBlank(log.getTargetId())) {
                log.setTargetId(resolveTargetId(signature.getParameterNames(), pjp.getArgs()));
            }
            // 写入失败不影响主流程；独立事务写入，避免主事务回滚导致审计丢失
            try {
                sysAuditLogService.saveInNewTx(log);
            } catch (Exception ignore) {
            }
        }
    }

    private HttpServletRequest resolveRequest() {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
            return attrs.getRequest();
        }
        return null;
    }

    private String resolveRequestId() {
        HttpServletRequest req = resolveRequest();
        if (req == null) {
            return UUID.randomUUID().toString();
        }
        String requestId = req.getHeader("X-Request-Id");
        if (StrUtil.isBlank(requestId)) {
            requestId = req.getHeader("X-Trace-Id");
        }
        return StrUtil.isBlank(requestId) ? UUID.randomUUID().toString() : requestId;
    }

    private String resolveClientIp(HttpServletRequest request) {
        // 简单处理：优先取 X-Forwarded-For 的第一个；否则取 remoteAddr
        String xff = request.getHeader("X-Forwarded-For");
        if (StrUtil.isNotBlank(xff)) {
            String first = xff.split(",")[0].trim();
            if (StrUtil.isNotBlank(first)) {
                return first;
            }
        }
        return request.getRemoteAddr();
    }

    private String serializeAndMask(String[] paramNames, Object[] args) {
        try {
            Map<String, Object> payload = new HashMap<>();
            if (paramNames != null && args != null && paramNames.length == args.length) {
                for (int i = 0; i < paramNames.length; i++) {
                    Object v = args[i];
                    // 避免把 request/response 之类塞进审计
                    if (v instanceof HttpServletRequest) {
                        continue;
                    }
                    payload.put(paramNames[i], v);
                }
            } else {
                payload.put("args", args);
            }
            String json = objectMapper.writeValueAsString(payload);
            return maskSecrets(json);
        } catch (Exception e) {
            return null;
        }
    }

    private String resolveTargetId(String[] paramNames, Object[] args) {
        if (paramNames == null || args == null || paramNames.length != args.length) {
            return null;
        }
        for (int i = 0; i < paramNames.length; i++) {
            String name = paramNames[i];
            if (name == null) {
                continue;
            }
            // 常见 ID 参数名
            if ("id".equalsIgnoreCase(name)
                    || "userId".equalsIgnoreCase(name)
                    || "roleId".equalsIgnoreCase(name)
                    || "permissionId".equalsIgnoreCase(name)
                    || "deptId".equalsIgnoreCase(name)
                    || "accountId".equalsIgnoreCase(name)
                    || "targetId".equalsIgnoreCase(name)) {
                Object v = args[i];
                if (v instanceof String s && StrUtil.isNotBlank(s)) {
                    return s;
                }
            }
        }
        return null;
    }

    private String maskSecrets(String json) {
        if (StrUtil.isBlank(json)) {
            return json;
        }
        return SECRET_FIELD_PATTERN.matcher(json).replaceAll("\"$1\":\"***\"");
    }
}
