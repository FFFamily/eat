package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.AdRoleDTO;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.AuditLog;
import com.tutu.common.annotation.PermissionRequired;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色控制器
 */
@RestController
@RequestMapping("/admin/role")
public class RoleController {
    
    @Autowired
    private AdRoleService adRoleService;
    
    /**
     * 分页查询角色列表
     */
    @PermissionRequired("role:list")
    @GetMapping("/page")
    public BaseResponse<IPage<AdRole>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<AdRole> page = adRoleService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询所有启用的角色
     */
    @PermissionRequired("role:list")
    @GetMapping("/all")
    public BaseResponse<List<AdRole>> findAllEnabled() {
        List<AdRole> adRoles = adRoleService.findAllEnabled();
        return BaseResponse.success(adRoles);
    }
    
    /**
     * 根据ID查询角色详情
     */
    @PermissionRequired("role:read")
    @GetMapping("/{id}")
    public BaseResponse<AdRole> getById(@PathVariable String id) {
        AdRole adRole = adRoleService.getById(id);
        if (adRole == null) {
            return BaseResponse.error("角色不存在");
        }
        return BaseResponse.success(adRole);
    }
    
    /**
     * 创建角色
     */
    @PermissionRequired("role:create")
    @AuditLog(action = "role.create", targetType = "role")
    @PostMapping
    public BaseResponse<String> createRole(@Valid @RequestBody AdRoleDTO dto) {
        AdRole entity = new AdRole();
        BeanUtils.copyProperties(dto, entity);
        adRoleService.createRole(entity);
        return  BaseResponse.success();
    }
    
    /**
     * 更新角色
     */
    @PermissionRequired("role:update")
    @AuditLog(action = "role.update", targetType = "role")
    @PutMapping
    public BaseResponse<String> updateRole(@Valid @RequestBody AdRoleDTO dto) {
        AdRole entity = new AdRole();
        BeanUtils.copyProperties(dto, entity);
        adRoleService.updateRole(entity);
        return  BaseResponse.success();
    }
    
    /**
     * 删除角色
     */
    @PermissionRequired("role:delete")
    @AuditLog(action = "role.delete", targetType = "role")
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteRole(@PathVariable String id) {
        adRoleService.deleteRole(id);
        return BaseResponse.success();
    }
    
    /**
     * 批量删除角色
     */
    @PermissionRequired("role:delete")
    @AuditLog(action = "role.batch_delete", targetType = "role")
    @DeleteMapping("/batch")
    public BaseResponse<String> batchDeleteRoles(@RequestBody List<String> ids) {
        try {
            boolean result = adRoleService.batchDeleteRoles(ids);
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
     * 分配权限
     */
    @PermissionRequired("role:bind_permissions")
    @AuditLog(action = "role.bind_permissions", targetType = "role")
    @PutMapping("/{id}/permissions")
    public BaseResponse<String> assignPermissions(@PathVariable String id, @RequestBody List<String> permissionIds) {
        adRoleService.assignPermissions(id, permissionIds);
        return  BaseResponse.success();
    }
    
    /**
     * 根据用户ID查询角色列表
     */
    @PermissionRequired("role:list")
    @GetMapping("/user/{userId}")
    public BaseResponse<List<AdRole>> findByUserId(@PathVariable String userId) {
        List<AdRole> adRoles = adRoleService.findByUserId(userId);
        return BaseResponse.success(adRoles);
    }
}
