package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("role")
public class Role extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 角色名称
    private String name;
    // 角色编码
    private String code;
    // 状态
    private String status;
    // 来源
    private String origin;
    // 权限
    private String permission;
}
