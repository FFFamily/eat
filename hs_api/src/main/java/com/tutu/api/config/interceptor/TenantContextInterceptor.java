package com.tutu.api.config.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.api.config.tenant.TenantProperties;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.tenant.TenantConstants;
import com.tutu.common.tenant.TenantContext;
import com.tutu.system.entity.SysTenant;
import com.tutu.system.service.SysTenantService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Resolve tenant from Header X-Tenant-Code or Sa-Token session and set TenantContext.
 *
 * Must run before any auth / business interceptor to avoid missing tenant condition in SQL.
 */
@Component
public class TenantContextInterceptor implements HandlerInterceptor {

    private final AntPathMatcher matcher = new AntPathMatcher();

    @Resource
    private TenantProperties tenantProperties;

    @Resource
    private SysTenantService sysTenantService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Defensive: ensure no ThreadLocal leak if the container reuses threads and some edge path skipped clearing.
        TenantContext.clear();

        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }

        String path = request.getServletPath();
        if (isIgnoredPath(path)) {
            return true;
        }

        // 1) If already logged in, session tenantId is the source of truth.
        if (StpUtil.isLogin()) {
            Object sessionTenantId = StpUtil.getTokenSession().get(TenantConstants.SESSION_TENANT_ID);
            String tenantId = sessionTenantId == null ? null : String.valueOf(sessionTenantId);
            if (StrUtil.isNotBlank(tenantId)) {
                TenantContext.setTenantId(tenantId);
            }
        }

        // 2) Header tenant code (required for login/register in strict mode).
        String tenantCode = request.getHeader(TenantConstants.HEADER_TENANT_CODE);
        if (StrUtil.isBlank(tenantCode)) {
            // If session already has tenantId, allow missing header.
            if (StrUtil.isNotBlank(TenantContext.getTenantId())) {
                return true;
            }
            if (tenantProperties.isStrict()) {
                throw new ServiceException("缺少租户编码请求头: " + TenantConstants.HEADER_TENANT_CODE);
            }
            // Compat mode: fall back to default tenant.
            TenantContext.setTenantId(tenantProperties.getDefaultTenantId());
            TenantContext.setTenantCode(tenantProperties.getDefaultTenantCode());
            return true;
        }

        SysTenant tenant = sysTenantService.getActiveByCode(tenantCode);
        TenantContext.setTenantId(tenant.getId());
        TenantContext.setTenantCode(tenant.getCode());

        // 3) If logged in, header must match session tenant (prevent token cross-tenant reuse).
        if (StpUtil.isLogin()) {
            Object sessionTenantId = StpUtil.getTokenSession().get(TenantConstants.SESSION_TENANT_ID);
            String sessionTid = sessionTenantId == null ? null : String.valueOf(sessionTenantId);
            if (StrUtil.isBlank(sessionTid)) {
                // Backward-compat: first request after upgrade, write it.
                StpUtil.getTokenSession().set(TenantConstants.SESSION_TENANT_ID, tenant.getId());
                StpUtil.getTokenSession().set(TenantConstants.SESSION_TENANT_CODE, tenant.getCode());
            } else if (!StrUtil.equals(sessionTid, tenant.getId())) {
                throw new ServiceException("租户不匹配：token 所属 tenantId=" + sessionTid + "，请求头租户 tenantId=" + tenant.getId());
            }
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        TenantContext.clear();
    }

    private boolean isIgnoredPath(String servletPath) {
        if (tenantProperties.getIgnorePaths() == null || tenantProperties.getIgnorePaths().isEmpty()) {
            return false;
        }
        for (String p : tenantProperties.getIgnorePaths()) {
            if (matcher.match(p, servletPath)) {
                return true;
            }
        }
        return false;
    }
}
