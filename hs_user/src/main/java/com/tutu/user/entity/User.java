package com.tutu.user.entity;

import com.alibaba.fastjson2.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import com.tutu.common.entity.user.BaseUserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user")
public class User extends BaseUserEntity {
    // id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 角色id
    private String roleId;

}
