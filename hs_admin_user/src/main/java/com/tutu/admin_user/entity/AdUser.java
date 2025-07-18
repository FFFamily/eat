package com.tutu.admin_user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.List;

import com.tutu.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 管理员用户实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AdUser extends BaseEntity {

    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 登录账号
     */
    private String username;

    /**
     * 账号密码
     */
    private String password;

    /**
     * 真实姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态：1-启用，0-禁用
     */
    private Integer status;

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
