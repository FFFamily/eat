package com.tutu.api.service;

import com.tutu.common.entity.user.PasswordUser;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.common.util.PasswordUtil;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.SysLoginLog;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.admin_user.service.SysLoginLogService;
import com.tutu.user.entity.Account;
import com.tutu.user.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
public class LoginService {
    @Resource
    private AccountService accountService;
    @Resource
    private AdUserService adUserService;
    @Resource
    private SysLoginLogService sysLoginLogService;

    /**
     * 通用登录校验逻辑
     * @param user 用户信息
     * @param loginPassword 登录密码
     */
    public void doLogin(String loginUsername, PasswordUser user, String loginPassword, String loginType) {
        try {
            if (user == null) {
                recordLoginLog(loginUsername, loginType, 0, "用户名或密码错误");
                throw new ServiceException("用户名或密码错误");
            }
            if (!PasswordUtil.match(loginPassword, user.getPassword())) {
                recordLoginLog(loginUsername, loginType, 0, "用户名或密码错误");
                throw new ServiceException("用户名或密码错误");
            }
            if (PasswordUtil.needsUpgrade(user.getPassword())) {
                String encoded = PasswordUtil.encode(loginPassword);
                if (user instanceof Account account) {
                    accountService.updatePasswordById(account.getId(), encoded);
                } else if (user instanceof AdUser adUser) {
                    adUserService.updatePasswordById(adUser.getId(), encoded);
                }
                user.setPassword(encoded);
            }
            if (!UserStatusEnum.USE.getCode().equals(user.getStatus())) {
                recordLoginLog(loginUsername, loginType, 0, "账号已禁用");
                throw new ServiceException("账号已被禁用");
            }
            recordLoginLog(loginUsername, loginType, 1, "登录成功");
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            recordLoginLog(loginUsername, loginType, 0, "登录异常");
            throw e;
        }
    }

    // 登录日志写入失败不影响主流程
    private void recordLoginLog(String username, String loginType, int success, String reason) {
        try {
            HttpServletRequest request = null;
            if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attrs) {
                request = attrs.getRequest();
            }

            SysLoginLog log = new SysLoginLog();
            log.setUsername(username);
            log.setLoginType(loginType);
            log.setSuccess(success);
            log.setReason(reason);
            if (request != null) {
                log.setIp(request.getRemoteAddr());
                log.setUa(request.getHeader("User-Agent"));
            }
            sysLoginLogService.save(log);
        } catch (Exception ignore) {
        }
    }
}
