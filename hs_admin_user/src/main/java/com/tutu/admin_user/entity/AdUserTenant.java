package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户-租户成员关系（全局表，不参与 TenantLine 过滤）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ad_user_tenant")
public class AdUserTenant extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String userId;

    private String tenantId;

    private String deptId;

    /**
     * 1=启用，0=禁用
     */
    private Integer status;
}

