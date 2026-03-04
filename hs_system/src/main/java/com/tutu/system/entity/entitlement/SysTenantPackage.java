package com.tutu.system.entity.entitlement;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 租户套餐订阅（tenant-scoped 表）
 */
@Getter
@Setter
@TableName("sys_tenant_package")
public class SysTenantPackage extends TenantBaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String packageId;

    private Date startTime;

    private Date endTime;

    /**
     * 状态：1-启用，0-停用
     */
    private Integer status;

    private String remark;
}

