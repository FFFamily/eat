package com.tutu.user.response;


import lombok.Data;

@Data
public class LoginUserResponse {
    private String id;
    // 登录账号
    private String username;
    // 用户头像
    private String avatar;
    // 用户昵称
    private String nickname;
    // 用户身份
    private String useType;
    // 用户评分
    private String score;
}
