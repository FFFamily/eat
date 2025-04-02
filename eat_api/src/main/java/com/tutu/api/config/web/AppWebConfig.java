package com.tutu.api.config.web;

import com.tutu.api.config.interceptor.AuthInterceptor;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class AppWebConfig implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 白名单
                .excludePathPatterns("/auth/login", "/auth/logout");
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
