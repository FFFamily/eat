package com.tutu.api.config.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Platform admin tenant scope:
 * - Default: GET/HEAD requests ignore TenantLine (cross-tenant read).
 * - Non-GET requests keep TenantLine enabled (avoid accidental cross-tenant writes).
 *
 * You can force cross-tenant on non-GET via header: X-Tenant-Scope=all (platform admin only).
 */
@Component
public class PlatformAdminTenantScopeInterceptor implements HandlerInterceptor {

    public static final String HEADER_TENANT_SCOPE = "X-Tenant-Scope";
    public static final String SCOPE_ALL = "all";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!StpUtil.isLogin()) return true;
        if (!AdminConstant.ADMIN_ID.equals(StpUtil.getLoginIdAsString())) return true;

        String method = request.getMethod();
        boolean isRead = HttpMethod.GET.matches(method) || HttpMethod.HEAD.matches(method);
        boolean forceAll = SCOPE_ALL.equalsIgnoreCase(request.getHeader(HEADER_TENANT_SCOPE));

        // Only ignore tenant isolation for read requests by default.
        if (isRead || forceAll) {
            TenantContext.setIgnoreTenantLine(true);
        }
        return true;
    }
}

