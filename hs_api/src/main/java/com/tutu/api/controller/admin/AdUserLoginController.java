package com.tutu.api.controller.admin;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.AdUserTenant;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.admin_user.service.AdUserTenantService;
import com.tutu.api.config.satoken.StpUtilPre;
import com.tutu.api.service.LoginService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.tenant.TenantConstants;
import com.tutu.common.tenant.TenantContext;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.user.request.LoginRequest;
import com.tutu.user.response.LoginUserResponse;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.tutu.system.entity.SysTenant;
import com.tutu.system.service.SysTenantService;
import com.tutu.system.service.entitlement.TenantEntitlementService;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(originPatterns = "*",allowCredentials="true",allowedHeaders = "*")
@RestController
@RequestMapping("/ad/auth")
public class AdUserLoginController {
    @Resource
    private AdUserService adUserService;
    @Resource
    private AdRoleService adRoleService;
    @Resource
    private LoginService loginService;
    @Resource
    private AdUserTenantService adUserTenantService;
    @Resource
    private SysTenantService sysTenantService;
    @Resource
    private TenantEntitlementService tenantEntitlementService;

    public record TenantOption(String tenantId, String tenantCode, String tenantName) {
    }

    public record PreLoginResp(String preToken, List<TenantOption> tenants) {
    }

    public record SelectTenantReq(String tenantId) {
    }

    /**
     * 登录（第一段）：校验账号密码，签发临时 Token（Pre-Token），用于后续“选择租户”。
     * @param loginRequest 登录请求
     * @return  preToken + tenant options
     */
    @PostMapping("/login")
    public BaseResponse<PreLoginResp> login(@RequestBody @Valid LoginRequest loginRequest) {
        // 获取用户信息
        String username = (loginRequest.getUsername() == null) ? null : loginRequest.getUsername().trim();
        AdUser user = adUserService.findByUsername(username);
        loginService.doLogin(loginRequest.getUsername(), user, loginRequest.getPassword(), "ad");

        // pre login
        StpUtilPre.login(user.getId());
        String preToken = StpUtilPre.getTokenValue();

        // return tenant options to reduce an extra roundtrip
        List<TenantOption> options = listTenantOptions(user.getId());
        if (options == null || options.isEmpty()) {
            // user has no enabled membership in any active tenant -> deny login
            StpUtilPre.logout();
            throw new ServiceException("账号未加入任何可用租户，无法登录");
        }
        return BaseResponse.success(new PreLoginResp(preToken, options));
    }

    /**
     * 获取当前用户可选租户列表（需要 Pre-Token）。
     */
    @GetMapping("/tenants")
    public BaseResponse<List<TenantOption>> tenants() {
        String userId = StpUtilPre.getLoginIdAsString();
        return BaseResponse.success(listTenantOptions(userId));
    }

    /**
     * 选择租户（第二段）：校验成员关系 + 租户状态 + 套餐有效，签发正式 Token-Key（绑定 tenantId）。
     */
    @PostMapping("/select-tenant")
    public BaseResponse<String> selectTenant(@Valid @RequestBody SelectTenantReq req) {
        String userId = StpUtilPre.getLoginIdAsString();
        if (req == null || StrUtil.isBlank(req.tenantId())) {
            throw new ServiceException("tenantId不能为空");
        }

        String tenantId = req.tenantId().trim();
        AdUserTenant link = adUserTenantService.getByUserIdAndTenantId(userId, tenantId);
        if (link == null) {
            throw new ServiceException("用户不属于该租户");
        }
        if (link.getStatus() != null && link.getStatus() == 0) {
            throw new ServiceException("用户在该租户下已被禁用");
        }

        SysTenant tenant = sysTenantService.getActiveById(tenantId);

        // Reject login if tenant has no active package
        boolean hasPkg = tenantEntitlementService.hasAnyActivePackage(tenantId, new Date());
        if (!hasPkg) {
            throw new ServiceException("租户未开通服务或已到期");
        }

        // Formal login: one tenant one token (use tenantId as Sa-Token device)
        StpUtil.login(userId, tenantId);
        String tokenValue = StpUtil.getTokenValue();
        // Bind tenant to this token's session explicitly (avoid ambiguity in 2-stage login flow).
        StpUtil.getTokenSessionByToken(tokenValue).set(TenantConstants.SESSION_TENANT_ID, tenantId);
        StpUtil.getTokenSessionByToken(tokenValue).set(TenantConstants.SESSION_TENANT_CODE, tenant.getCode());

        // Invalidate pre token
        StpUtilPre.logout();

        return BaseResponse.success(tokenValue);
    }

    /**
     * 登出
     */
    @GetMapping("/logout")
    public BaseResponse<Void> logout() {
        StpUtil.logout();
        return BaseResponse.success();
    }
    /**
     * 获取当前登录用户信息
     * @return 当前登录用户信息
     */
    @GetMapping("/getLoginInfo")
    public BaseResponse<LoginUserResponse> getUserInfo() {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        // 使用 AdUserService 获取用户信息
        AdUser user = adUserService.getById(userId);
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        LoginUserResponse userInfo = new LoginUserResponse();
        BeanUtil.copyProperties(user, userInfo);
        return BaseResponse.success(userInfo);
    }

    private List<TenantOption> listTenantOptions(String userId) {
        if (StrUtil.isBlank(userId)) return List.of();

        List<AdUserTenant> links = adUserTenantService.listByUserId(userId);
        if (links == null || links.isEmpty()) return List.of();

        Set<String> tenantIds = links.stream()
                .filter(l -> l != null && l.getStatus() != null && l.getStatus() == 1)
                .map(AdUserTenant::getTenantId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        if (tenantIds.isEmpty()) return List.of();

        Map<String, SysTenant> tenantMap = sysTenantService.listActiveByIds(tenantIds).stream()
                .collect(Collectors.toMap(SysTenant::getId, t -> t, (a, b) -> a));

        return tenantIds.stream()
                .map(tenantMap::get)
                .filter(t -> t != null)
                .map(t -> new TenantOption(t.getId(), t.getCode(), t.getName()))
                .toList();
    }
}
