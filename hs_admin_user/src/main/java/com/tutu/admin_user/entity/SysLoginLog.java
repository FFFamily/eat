package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.TenantBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志（后台基础能力）
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_login_log")
public class SysLoginLog extends TenantBaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String username;

    /**
     * 登录端类型：ad / wx / api 等
     */
    private String loginType;

    /**
     * 1 成功；0 失败
     */
    private Integer success;

    private String reason;

    private String ip;

    private String ua;
}

