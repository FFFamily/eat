package com.tutu.user.response;


import lombok.Data;

@Data
public class LoginUserResponse {
    private String id;
    // 登录账号
    private String username;
    // 头像
    private String avatar;
    // 用户状态
    private String status;
    // 用户昵称
    private String nickname;
    // 角色
    private String roleId;
}
