package com.tutu.system.dto.entitlement;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.Set;

/**
 * 当前租户在某个时间点的授权快照。
 *
 * - activeModuleIds: 当前生效的模块集合（用于 URL 前缀拦截）
 * - allowedPermissionIds: 租户“可分配/可拥有”的权限点上限（用于 RBAC 交集计算）
 */
@Data
public class EntitlementSnapshot {

    private String tenantId;

    private Date loadedAt;

    private Set<String> activeModuleIds = Collections.emptySet();

    private Set<String> activeModuleCodes = Collections.emptySet();

    private Set<String> allowedPermissionIds = Collections.emptySet();
}

