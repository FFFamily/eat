package com.tutu.common.tenant;

/**
 * Multi-tenant constants shared across modules.
 */
public final class TenantConstants {
    private TenantConstants() {}

    /**
     * Request header that carries tenant code.
     */
    public static final String HEADER_TENANT_CODE = "X-Tenant-Code";

    /**
     * Sa-Token token session keys.
     */
    public static final String SESSION_TENANT_ID = "tenantId";
    public static final String SESSION_TENANT_CODE = "tenantCode";
}

