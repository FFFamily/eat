package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 租户实体（全局表，不参与租户隔离过滤）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_tenant")
public class SysTenant extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 租户编码（前端 Header：X-Tenant-Code）
     */
    private String code;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

    private String remark;
}

