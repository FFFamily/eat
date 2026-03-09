package com.tutu.api.config.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.api.config.tenant.TenantProperties;
import com.tutu.common.constant.AdminConstant;
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

        // Session tenantId is the only source of truth after the system switches to
        // "global user + choose tenant after password verification" flow.
        if (StpUtil.isLogin()) {
            Object sessionTenantId = StpUtil.getTokenSession().get(TenantConstants.SESSION_TENANT_ID);
            String tenantId = sessionTenantId == null ? null : String.valueOf(sessionTenantId);
            if (StrUtil.isBlank(tenantId)) {
                // Platform admin may operate without tenant context on platform-only endpoints.
                if (AdminConstant.ADMIN_ID.equals(StpUtil.getLoginIdAsString())) {
                    return true;
                }
                throw new ServiceException("缺少租户信息，请重新登录并选择租户");
            }
            TenantContext.setTenantId(tenantId);

            Object sessionTenantCode = StpUtil.getTokenSession().get(TenantConstants.SESSION_TENANT_CODE);
            String tenantCode = sessionTenantCode == null ? null : String.valueOf(sessionTenantCode);
            if (StrUtil.isNotBlank(tenantCode)) {
                TenantContext.setTenantCode(tenantCode);
            }
        }

        // Backward/compat: allow unauthenticated requests to carry tenant header (e.g. legacy /wx/auth/login).
        // Formal logged-in requests must rely on token session tenantId.
        if (!StpUtil.isLogin()) {
            String tenantCode = request.getHeader(TenantConstants.HEADER_TENANT_CODE);
            if (StrUtil.isNotBlank(tenantCode)) {
                SysTenant tenant = sysTenantService.getActiveByCode(tenantCode);
                TenantContext.setTenantId(tenant.getId());
                TenantContext.setTenantCode(tenant.getCode());
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
