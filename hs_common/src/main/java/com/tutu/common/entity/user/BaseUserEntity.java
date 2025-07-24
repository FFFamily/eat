package com.tutu.common.entity.user;

import com.alibaba.fastjson2.annotation.JSONField;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseUserEntity extends BaseEntity {
    // 登录账号
    private String username;
    // 登录密码
    @JSONField(serialize = false)
    private String password;
    // 用户状态
    private String status;
    // 用户昵称
    private String nickname;
}
