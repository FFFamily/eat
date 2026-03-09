package com.tutu.api.config.interceptor;

import com.tutu.api.config.satoken.StpUtilPre;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Pre-auth interceptor: protects endpoints that require a short-lived "pre token"
 * (issued after username/password verification, before tenant selection).
 */
@Component
public class PreAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (HttpMethod.OPTIONS.matches(request.getMethod())) {
            return true;
        }
        StpUtilPre.checkLogin();
        return true;
    }
}

