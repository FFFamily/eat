package com.tutu.api.controller.admin.entitlement;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.entitlement.SysPackage;
import com.tutu.system.service.entitlement.SysPackageService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 平台：套餐管理（全局能力）
 */
@RestController
@RequestMapping("/admin/package")
public class PackageAdminController {

    @Resource
    private SysPackageService sysPackageService;

    private void checkPlatformAdmin() {
        StpUtil.checkLogin();
        if (!AdminConstant.ADMIN_ID.equals(StpUtil.getLoginIdAsString())) {
            throw new ServiceException("仅平台管理员可访问");
        }
    }

    public record PackageUpsertReq(
            String id,
            @NotBlank String code,
            @NotBlank String name,
            Integer status,
            Integer sortOrder,
            String remark
    ) {
    }

    public record PackageResp(
            String id,
            String code,
            String name,
            Integer status,
            Integer sortOrder,
            String remark,
            Date createTime,
            Date updateTime
    ) {
        static PackageResp from(SysPackage p) {
            return new PackageResp(
                    p.getId(),
                    p.getCode(),
                    p.getName(),
                    p.getStatus(),
                    p.getSortOrder(),
                    p.getRemark(),
                    p.getCreateTime(),
                    p.getUpdateTime()
            );
        }
    }

    @GetMapping("/page")
    public BaseResponse<IPage<PackageResp>> page(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status
    ) {
        checkPlatformAdmin();
        Page<SysPackage> page = new Page<>(current, size);
        LambdaQueryWrapper<SysPackage> w = new LambdaQueryWrapper<>();
        w.eq(SysPackage::getIsDeleted, CommonConstant.NO_STR);
        if (StrUtil.isNotBlank(keyword)) {
            w.and(q -> q.like(SysPackage::getCode, keyword).or().like(SysPackage::getName, keyword));
        }
        if (status != null) w.eq(SysPackage::getStatus, status);
        w.orderByAsc(SysPackage::getSortOrder).orderByDesc(SysPackage::getUpdateTime);
        IPage<SysPackage> res = sysPackageService.page(page, w);
        return BaseResponse.success(res.convert(PackageResp::from));
    }

    @GetMapping("/all")
    public BaseResponse<List<PackageResp>> all(@RequestParam(required = false) Integer status) {
        checkPlatformAdmin();
        LambdaQueryWrapper<SysPackage> w = new LambdaQueryWrapper<>();
        w.eq(SysPackage::getIsDeleted, CommonConstant.NO_STR);
        if (status != null) w.eq(SysPackage::getStatus, status);
        w.orderByAsc(SysPackage::getSortOrder).orderByDesc(SysPackage::getUpdateTime);
        return BaseResponse.success(sysPackageService.list(w).stream().map(PackageResp::from).toList());
    }

    @GetMapping("/{id}")
    public BaseResponse<PackageResp> get(@PathVariable String id) {
        checkPlatformAdmin();
        SysPackage p = sysPackageService.getById(id);
        if (p == null) return BaseResponse.error("套餐不存在");
        return BaseResponse.success(PackageResp.from(p));
    }

    @PostMapping
    public BaseResponse<String> create(@Valid @RequestBody PackageUpsertReq req) {
        checkPlatformAdmin();
        SysPackage p = new SysPackage();
        p.setCode(req.code());
        p.setName(req.name());
        p.setStatus(req.status() == null ? CommonConstant.YES_INT : req.status());
        p.setSortOrder(req.sortOrder() == null ? 0 : req.sortOrder());
        p.setRemark(req.remark());
        p.setIsDeleted(CommonConstant.NO_STR);
        sysPackageService.save(p);
        return BaseResponse.success(p.getId());
    }

    @PutMapping
    public BaseResponse<Void> update(@Valid @RequestBody PackageUpsertReq req) {
        checkPlatformAdmin();
        if (StrUtil.isBlank(req.id())) return BaseResponse.error("缺少id");
        SysPackage exist = sysPackageService.getById(req.id());
        if (exist == null) return BaseResponse.error("套餐不存在");
        exist.setName(req.name());
        exist.setStatus(req.status() == null ? exist.getStatus() : req.status());
        exist.setSortOrder(req.sortOrder() == null ? exist.getSortOrder() : req.sortOrder());
        exist.setRemark(req.remark());
        sysPackageService.updateById(exist);
        return BaseResponse.success();
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Void> delete(@PathVariable String id) {
        checkPlatformAdmin();
        sysPackageService.removeById(id);
        return BaseResponse.success();
    }

    @GetMapping("/{id}/permission-ids")
    public BaseResponse<List<String>> listPermissionIds(@PathVariable String id) {
        checkPlatformAdmin();
        return BaseResponse.success(sysPackageService.listBoundPermissionIds(id));
    }

    @PutMapping("/{id}/permission-ids")
    public BaseResponse<Void> setPermissionIds(@PathVariable String id, @RequestBody List<String> permissionIds) {
        checkPlatformAdmin();
        sysPackageService.setPackagePermissions(id, permissionIds);
        return BaseResponse.success();
    }
}
