package com.tutu.common.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity for tenant-scoped tables.
 */
@Getter
@Setter
public class TenantBaseEntity extends BaseEntity {

    /**
     * Tenant ID for row-level isolation.
     */
    @TableField(fill = FieldFill.INSERT)
    private String tenantId;
}

