package com.tutu.api.controller.admin.tenant;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.api.service.TenantProvisionService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.dto.tenant.SysTenantCreateDTO;
import com.tutu.system.dto.tenant.SysTenantUpdateDTO;
import com.tutu.system.dto.tenant.TenantBootstrapAdminDTO;
import com.tutu.system.entity.SysTenant;
import com.tutu.system.service.SysTenantService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 平台租户管理（全局能力）
 *
 * 注意：为避免普通租户 SUPER_ADMIN 越权管理其他租户，这里仅允许 platform admin（固定 userId=1）访问。
 */
@RestController
@RequestMapping("/admin/tenant")
public class TenantAdminController {

    @Resource
    private SysTenantService sysTenantService;
    @Resource
    private TenantProvisionService tenantProvisionService;

    private void checkPlatformAdmin() {
        StpUtil.checkLogin();
        String loginId = StpUtil.getLoginIdAsString();
        if (!AdminConstant.ADMIN_ID.equals(String.valueOf(loginId))) {
            throw new ServiceException("仅平台管理员可管理租户");
        }
    }

    @GetMapping("/page")
    public BaseResponse<IPage<SysTenant>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        checkPlatformAdmin();
        Page<SysTenant> page = new Page<>(current, size);
        LambdaQueryWrapper<SysTenant> w = new LambdaQueryWrapper<>();
        w.eq(SysTenant::getIsDeleted, CommonConstant.NO_STR);
        if (StrUtil.isNotBlank(keyword)) {
            w.and(q -> q.like(SysTenant::getCode, keyword).or().like(SysTenant::getName, keyword));
        }
        if (status != null) {
            w.eq(SysTenant::getStatus, status);
        }
        w.orderByDesc(SysTenant::getCreateTime);
        return BaseResponse.success(sysTenantService.page(page, w));
    }

    @GetMapping("/{id}")
    public BaseResponse<SysTenant> getById(@PathVariable String id) {
        checkPlatformAdmin();
        SysTenant tenant = sysTenantService.getById(id);
        if (tenant == null) {
            return BaseResponse.error("租户不存在");
        }
        return BaseResponse.success(tenant);
    }

    @PostMapping
    public BaseResponse<String> create(@Valid @RequestBody SysTenantCreateDTO dto) {
        checkPlatformAdmin();
        // Code must be unique across all non-deleted tenants (regardless of status).
        LambdaQueryWrapper<SysTenant> w = new LambdaQueryWrapper<>();
        w.eq(SysTenant::getCode, dto.getCode())
                .eq(SysTenant::getIsDeleted, CommonConstant.NO_STR)
                .last("limit 1");
        if (sysTenantService.getOne(w) != null) {
            return BaseResponse.error("租户编码已存在: " + dto.getCode());
        }

        SysTenant tenant = new SysTenant();
        tenant.setCode(dto.getCode());
        tenant.setName(dto.getName());
        tenant.setRemark(dto.getRemark());
        tenant.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        tenant.setIsDeleted(CommonConstant.NO_STR);
        tenant.setCreateTime(new Date());
        tenant.setUpdateTime(new Date());
        sysTenantService.save(tenant);
        return BaseResponse.success(tenant.getId());
    }

    @PutMapping
    public BaseResponse<Void> update(@Valid @RequestBody SysTenantUpdateDTO dto) {
        checkPlatformAdmin();
        SysTenant exist = sysTenantService.getById(dto.getId());
        if (exist == null) {
            return BaseResponse.error("租户不存在");
        }
        if ("t1".equals(exist.getId())) {
            // Prevent accidentally breaking default tenant.
            return BaseResponse.error("默认租户不允许修改");
        }

        exist.setName(dto.getName());
        if (dto.getStatus() != null) {
            exist.setStatus(dto.getStatus());
        }
        exist.setRemark(dto.getRemark());
        exist.setUpdateTime(new Date());
        sysTenantService.updateById(exist);
        return BaseResponse.success();
    }

    @PutMapping("/{id}/status")
    public BaseResponse<Void> changeStatus(@PathVariable String id, @RequestParam Integer status) {
        checkPlatformAdmin();
        SysTenant exist = sysTenantService.getById(id);
        if (exist == null) {
            return BaseResponse.error("租户不存在");
        }
        if ("t1".equals(exist.getId())) {
            return BaseResponse.error("默认租户不允许停用");
        }
        exist.setStatus(status);
        exist.setUpdateTime(new Date());
        sysTenantService.updateById(exist);
        return BaseResponse.success();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable String id) {
        checkPlatformAdmin();
        SysTenant exist = sysTenantService.getById(id);
        if (exist == null) {
            return BaseResponse.success();
        }
        if ("t1".equals(exist.getId())) {
            return BaseResponse.error("默认租户不允许删除");
        }
        // Only allow deleting disabled tenants to reduce risk.
        if (exist.getStatus() != null && exist.getStatus() == 1) {
            return BaseResponse.error("仅允许删除已停用的租户");
        }
        sysTenantService.removeById(id);
        return BaseResponse.success();
    }

    /**
     * 为指定租户初始化一个后台超级管理员账号，并创建 SUPER_ADMIN 角色（若不存在）。
     */
    @PostMapping("/{id}/bootstrap-admin")
    public BaseResponse<String> bootstrapAdmin(@PathVariable String id, @Valid @RequestBody TenantBootstrapAdminDTO dto) {
        checkPlatformAdmin();
        String userId = tenantProvisionService.bootstrapTenantAdmin(id, dto);
        return BaseResponse.success(userId);
    }
}

