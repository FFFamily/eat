package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.AdPermissionDTO;
import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.service.AdPermissionService;
import com.tutu.common.Response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping("/page")
    public BaseResponse<IPage<AdPermission>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<AdPermission> page = permissionService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 查询权限树
     */
    @GetMapping("/tree")
    public BaseResponse<List<AdPermission>> getPermissionTree() {
        List<AdPermission> tree = permissionService.getMenuTree();
        return BaseResponse.success(tree);
    }
    
    /**
     * 根据ID查询权限详情
     */
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
    @PostMapping
    public BaseResponse<String> createPermission(@Valid @RequestBody AdPermission adPermissionDTO) {
        try {
            boolean result = permissionService.createPermission(adPermissionDTO);
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
    public BaseResponse<String> updatePermission(@Valid @RequestBody AdPermission adPermissionDTO) {
        try {
            boolean result = permissionService.updatePermission(adPermissionDTO);
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
    public BaseResponse<List<AdPermission>> findByRoleId(@PathVariable String roleId) {
        List<AdPermission> adPermissions = permissionService.findByRoleId(roleId);
        return BaseResponse.success(adPermissions);
    }
    
    /**
     * 根据用户ID查询权限列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<AdPermission>> findByUserId(@PathVariable String userId) {
        List<AdPermission> adPermissions = permissionService.findByUserId(userId);
        return BaseResponse.success(adPermissions);
    }
}
