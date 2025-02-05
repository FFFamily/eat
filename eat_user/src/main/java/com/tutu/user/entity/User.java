package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@TableName("user")
public class User extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 登录账号
    private String username;
    // 账号密码
    private String password;
    // 用户状态
    private String status;
    // 用户昵称
    private String nickname;
    // 角色
    private String roleId;

}
