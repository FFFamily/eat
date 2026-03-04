package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 审计日志（后台基础能力）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_audit_log")
public class SysAuditLog extends TenantBaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String actorUserId;

    private String action;

    private String targetType;

    private String targetId;

    private String detailJson;

    /**
     * 1 成功；0 失败
     */
    private Integer result;

    private String ip;

    private String ua;

    private String requestId;

    private String errorMessage;
}

