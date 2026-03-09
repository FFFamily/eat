package com.tutu.api.controller.admin.tenant;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.tenant.TenantRunAs;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 平台：以指定 tenantId 运行租户侧查询（run-as-tenant）。
 *
 * 注意：该接口建议仅用于平台排障/运维，不应暴露给租户侧账号。
 */
@RestController
@RequestMapping("/admin/tenant/{tenantId}/runas")
public class TenantRunAsAdminController {

    @Resource
    private AdUserService adUserService;

    private void checkPlatformAdmin() {
        StpUtil.checkLogin();
        if (!AdminConstant.ADMIN_ID.equals(StpUtil.getLoginIdAsString())) {
            throw new ServiceException("仅平台管理员可访问");
        }
    }

    @GetMapping("/user/page")
    public BaseResponse<IPage<AdUser>> tenantUserPage(
            @PathVariable String tenantId,
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deptId
    ) {
        checkPlatformAdmin();
        IPage<AdUser> page = TenantRunAs.run(tenantId, () -> adUserService.getPageList(current, size, keyword, status, deptId));
        if (page != null && page.getRecords() != null) {
            page.getRecords().forEach(u -> {
                if (u != null) u.setPassword(null);
            });
        }
        return BaseResponse.success(page);
    }
}

