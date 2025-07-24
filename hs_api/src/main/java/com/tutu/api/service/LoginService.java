package com.tutu.api.service;

import cn.dev33.satoken.secure.SaSecureUtil;
import com.tutu.common.entity.user.BaseUserEntity;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.enums.user.UserStatusEnum;
import org.springframework.stereotype.Service;

@Service
public class LoginService {

    /**
     * 通用登录校验逻辑
     * @param user 用户信息
     * @param loginPassword 登录密码
     */
    public void doLogin(BaseUserEntity user,String loginPassword) {
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        // 验证密码（假设密码已经加密存储）
        String md5 = SaSecureUtil.md5(loginPassword);
        if (!md5.equals(user.getPassword())){
            throw new ServiceException("密码错误");
        }
        // 检查用户状态
        if (!UserStatusEnum.USE.getCode().equals(user.getStatus())) {
            throw new ServiceException("账号已被禁用");
        }
    }
}
