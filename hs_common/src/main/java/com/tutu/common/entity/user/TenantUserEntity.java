package com.tutu.common.entity.user;

import com.alibaba.fastjson2.annotation.JSONField;
import com.tutu.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity for tenant-scoped user tables (has tenantId column and participates in TenantLine isolation).
 */
@Getter
@Setter
public class TenantUserEntity extends TenantBaseEntity implements PasswordUser {

    private String username;

    @JSONField(serialize = false)
    private String password;

    private String status;

    private String nickname;
}

