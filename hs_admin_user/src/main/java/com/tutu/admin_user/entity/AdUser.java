package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.util.List;

import com.tutu.common.entity.user.BaseUserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdUser extends BaseUserEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 真实姓名
     */
    private String nickname;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;
    /**
     * 部门ID
     */
    private String deptId;

    /**
     * 角色列表（非数据库字段）
     */
    @TableField(exist = false)
    private List<AdRole> adRoles;

    /**
     * 部门信息（非数据库字段）
     */
    @TableField(exist = false)
    private AdDepartment adDepartment;
}
