package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

import com.tutu.common.entity.user.BaseUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("ad_user")
public class AdUser extends BaseUserEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;
    /**
     * 部门ID（租户内属性：来自 ad_user_tenant.dept_id；ad_user 表不再持久化该字段）
     */
    @TableField(exist = false)
    private String deptId;

    /**
     * 角色列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<AdRole> adRoles;

    /**
     * 部门 信息（非数据库字段）
     */
    @TableField(exist = false)
    private AdDepartment adDepartment;

    /**
     * 部门名称（非数据库字段，便于列表直接展示）
     */
    @TableField(exist = false)
    private String deptName;
}
