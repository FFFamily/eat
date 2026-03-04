package com.tutu.common.tenant;

import cn.hutool.core.util.StrUtil;
import com.tutu.common.exceptions.ServiceException;

/**
 * Per-request tenant context (backed by ThreadLocal).
 *
 * Must be set at request entry and cleared at request end.
 */
public final class TenantContext {
    private TenantContext() {}

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();
    private static final ThreadLocal<String> TENANT_CODE = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static String getRequiredTenantId() {
        String tenantId = TENANT_ID.get();
        if (StrUtil.isBlank(tenantId)) {
            throw new ServiceException("缺少租户上下文(tenantId)，请检查是否携带 X-Tenant-Code 或登录会话是否写入 tenantId");
        }
        return tenantId;
    }

    public static void setTenantCode(String tenantCode) {
        TENANT_CODE.set(tenantCode);
    }

    public static String getTenantCode() {
        return TENANT_CODE.get();
    }

    public static void clear() {
        TENANT_ID.remove();
        TENANT_CODE.remove();
    }
}

