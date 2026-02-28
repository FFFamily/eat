package com.tutu.api.controller.admin.user;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.AdPermissionDTO;
import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.service.AdPermissionService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.AuditLog;
import com.tutu.common.annotation.PermissionRequired;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/admin/permission")
public class PermissionController {
    
    @Autowired
    private AdPermissionService permissionService;
    
    /**
     * 分页查询权限列表
     */
    @PermissionRequired("permission:list")
    @GetMapping("/page")
    public BaseResponse<IPage<AdPermission>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status) {
        IPage<AdPermission> page = permissionService.getPageList(current, size, keyword, type, status);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询权限树
     */
    @PermissionRequired("permission:list")
    @GetMapping("/tree")
    public BaseResponse<List<AdPermission>> getPermissionTree() {
        List<AdPermission> tree = permissionService.getPermissionTree();
        return BaseResponse.success(tree);
    }
    
    /**
     * 根据ID查询权限详情
     */
    @PermissionRequired("permission:list")
    @GetMapping("/{id}")
    public BaseResponse<AdPermission> getById(@PathVariable String id) {
        AdPermission adPermission = permissionService.getById(id);
        if (adPermission == null) {
            return BaseResponse.error("权限不存在");
        }
        return BaseResponse.success(adPermission);
    }
    
    /**
     * 创建权限
     */
    @PermissionRequired("permission:create")
    @AuditLog(action = "permission.create", targetType = "permission")
    @PostMapping
    public BaseResponse<String> createPermission(@Valid @RequestBody AdPermissionDTO dto) {
        try {
            AdPermission entity = new AdPermission();
            BeanUtils.copyProperties(dto, entity);
            boolean result = permissionService.createPermission(entity);
            if (result) {
                return BaseResponse.success("创建成功");
            } else {
                return BaseResponse.error("创建失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新权限
     */
    @PermissionRequired("permission:update")
    @AuditLog(action = "permission.update", targetType = "permission")
    @PutMapping
    public BaseResponse<String> updatePermission(@Valid @RequestBody AdPermissionDTO dto) {
        try {
            AdPermission entity = new AdPermission();
            BeanUtils.copyProperties(dto, entity);
            boolean result = permissionService.updatePermission(entity);
            if (result) {
                return BaseResponse.success("更新成功");
            } else {
                return BaseResponse.error("更新失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 删除权限
     */
    @PermissionRequired("permission:delete")
    @AuditLog(action = "permission.delete", targetType = "permission")
    @DeleteMapping("/{id}")
    public BaseResponse<String> deletePermission(@PathVariable String id) {
        try {
            boolean result = permissionService.deletePermission(id);
            if (result) {
                return BaseResponse.success("删除成功");
            } else {
                return BaseResponse.error("删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 批量删除权限
     */
    @PermissionRequired("permission:delete")
    @AuditLog(action = "permission.batch_delete", targetType = "permission")
    @DeleteMapping("/batch")
    public BaseResponse<String> batchDeletePermissions(@RequestBody List<String> ids) {
        try {
            boolean result = permissionService.batchDeletePermissions(ids);
            if (result) {
                return BaseResponse.success("批量删除成功");
            } else {
                return BaseResponse.error("批量删除失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据角色ID查询权限列表
     */
    @PermissionRequired("permission:list")
    @GetMapping("/role/{roleId}")
    public BaseResponse<List<AdPermission>> findByRoleId(@PathVariable String roleId) {
        List<AdPermission> adPermissions = permissionService.findByRoleId(roleId);
        return BaseResponse.success(adPermissions);
    }
    
    /**
     * 根据用户ID查询权限列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<AdPermission>> findByUserId(@PathVariable String userId) {
        // Used by frontend right after login to decide menu/button visibility.
        // Allow querying "my own" permissions without requiring `permission:list`,
        // otherwise users without后台管理权限会在登录后立刻被 401/弹窗卡住。
        StpUtil.checkLogin();
        String loginId = StpUtil.getLoginIdAsString();
        if (!Objects.equals(String.valueOf(userId), String.valueOf(loginId))) {
            StpUtil.checkPermission("permission:list");
        }
        List<AdPermission> adPermissions = permissionService.findByUserId(userId);
        return BaseResponse.success(adPermissions);
    }
}
