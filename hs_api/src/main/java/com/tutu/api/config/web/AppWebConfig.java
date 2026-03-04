package com.tutu.api.config.web;

import com.tutu.api.config.interceptor.AuthInterceptor;
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
    private TenantContextInterceptor tenantContextInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Must run first to ensure TenantContext is available for SQL tenant interceptor.
        registry.addInterceptor(tenantContextInterceptor)
                .addPathPatterns("/**");

        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 白名单
                .excludePathPatterns("/ad/auth/login",
                        "/wx/auth/login",
                        "/wx/auth/register",
                        "/ad/auth/register",
                        "/system/file/noAuth/upload", // 免登录上传
                        "/files/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
