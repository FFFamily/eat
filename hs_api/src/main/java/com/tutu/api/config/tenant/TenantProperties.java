package com.tutu.api.config.tenant;

import com.tutu.common.tenant.TenantConstants;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "tenant")
public class TenantProperties {

    /**
     * Strict mode: if true, requests (except ignored paths) must carry X-Tenant-Code when session does not have tenant.
     */
    private boolean strict = false;

    /**
     * Default tenant used in compat mode when X-Tenant-Code is absent and user not logged in.
     */
    private String defaultTenantId = "t1";

    private String defaultTenantCode = "default";

    /**
     * Paths that do not require tenant header / tenant context (e.g. static file serving).
     */
    private List<String> ignorePaths = new ArrayList<>(List.of(
            "/files/**",
            "/actuator/**",
            "/druid/**",
            // Platform (global) tenant management endpoints shouldn't require resolving a tenant first.
            "/admin/tenant/**"
    ));

    public String tenantHeaderName() {
        return TenantConstants.HEADER_TENANT_CODE;
    }
}
