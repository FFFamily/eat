package com.tutu.api.config.web;

import com.tutu.api.config.interceptor.AuthInterceptor;
import com.tutu.api.config.interceptor.PlatformAdminTenantScopeInterceptor;
import com.tutu.api.config.interceptor.PreAuthInterceptor;
import com.tutu.api.config.interceptor.TenantContextInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class AppWebConfig implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;
    @Resource
    private PreAuthInterceptor preAuthInterceptor;
    @Resource
    private TenantContextInterceptor tenantContextInterceptor;
    @Resource
    private PlatformAdminTenantScopeInterceptor platformAdminTenantScopeInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Must run first to ensure TenantContext is available for SQL tenant interceptor.
        registry.addInterceptor(tenantContextInterceptor)
                .addPathPatterns("/**");

        // Pre-auth endpoints: require pre token (after password verified, before tenant selected).
        registry.addInterceptor(preAuthInterceptor)
                .addPathPatterns(
                        "/ad/auth/tenants",
                        "/ad/auth/select-tenant"
                );

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 白名单
                .excludePathPatterns("/ad/auth/login",
                        "/ad/auth/tenants",
                        "/ad/auth/select-tenant",
                        "/wx/auth/login",
                        "/wx/auth/register",
                        "/ad/auth/register",
                        "/system/file/noAuth/upload", // 免登录上传
                        "/files/**");

        // Platform admin can view cross-tenant data on read operations.
        // Must be registered after auth so we can safely identify platform admin.
        registry.addInterceptor(platformAdminTenantScopeInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/ad/auth/**",
                        "/wx/auth/**",
                        "/files/**"
                );
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
