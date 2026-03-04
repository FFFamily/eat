package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.TenantBaseEntity;
import lombok.Getter;
import lombok.Setter;
// 用户角色关联表
@Getter
@Setter
public class UserRole extends TenantBaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String userId;
    private String roleId;
}
