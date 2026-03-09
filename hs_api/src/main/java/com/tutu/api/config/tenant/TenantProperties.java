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
     * Legacy flag (kept for compatibility). The system now resolves tenant only from token session after login.
     */
    private boolean strict = false;

    /**
     * Legacy default tenant (kept for compatibility).
     */
    private String defaultTenantId = "t1";

    private String defaultTenantCode = "default";

    /**
     * Paths that do not require tenant context (e.g. static file serving or platform-only endpoints).
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
