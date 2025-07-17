package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
// 用户角色关联表
@Getter
@Setter
@TableName("user_role")
public class UserRole extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String userId;
    private String roleId;
}
