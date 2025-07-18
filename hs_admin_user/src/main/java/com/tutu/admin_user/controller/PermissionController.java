package com.tutu.admin_user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.PermissionDTO;
import com.tutu.admin_user.entity.Permission;
import com.tutu.admin_user.service.PermissionService;
import com.tutu.common.Response.BaseResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 权限控制器
 */
@RestController
@RequestMapping("/admin/permission")
public class PermissionController {
    
    @Autowired
    private PermissionService permissionService;
    
    /**
     * 分页查询权限列表
     */
    @GetMapping("/page")
    public BaseResponse<IPage<Permission>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<Permission> page = permissionService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询权限树
     */
    @GetMapping("/tree")
    public BaseResponse<List<Permission>> getPermissionTree() {
        List<Permission> tree = permissionService.getMenuTree();
        return BaseResponse.success(tree);
    }
    
    /**
     * 根据ID查询权限详情
     */
    @GetMapping("/{id}")
    public BaseResponse<Permission> getById(@PathVariable String id) {
        Permission permission = permissionService.getById(id);
        if (permission == null) {
            return BaseResponse.error("权限不存在");
        }
        return BaseResponse.success(permission);
    }
    
    /**
     * 创建权限
     */
    @PostMapping
    public BaseResponse<String> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        try {
            boolean result = permissionService.createPermission(permissionDTO);
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
    @PutMapping
    public BaseResponse<String> updatePermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        try {
            boolean result = permissionService.updatePermission(permissionDTO);
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
    @GetMapping("/role/{roleId}")
    public BaseResponse<List<Permission>> findByRoleId(@PathVariable String roleId) {
        List<Permission> permissions = permissionService.findByRoleId(roleId);
        return BaseResponse.success(permissions);
    }
    
    /**
     * 根据用户ID查询权限列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<Permission>> findByUserId(@PathVariable String userId) {
        List<Permission> permissions = permissionService.findByUserId(userId);
        return BaseResponse.success(permissions);
    }
}
