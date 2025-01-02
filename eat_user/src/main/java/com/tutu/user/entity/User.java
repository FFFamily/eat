package com.tutu.user.entity;

import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseEntity {
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
