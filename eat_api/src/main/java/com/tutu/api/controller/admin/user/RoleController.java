package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.AdRoleDTO;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.common.Response.BaseResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
    @GetMapping("/all")
    public BaseResponse<List<AdRole>> findAllEnabled() {
        List<AdRole> adRoles = adRoleService.findAllEnabled();
        return BaseResponse.success(adRoles);
    }
    
    /**
     * 根据ID查询角色详情
     */
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
    @PostMapping
    public BaseResponse<String> createRole(@Valid @RequestBody AdRoleDTO adRoleDTO) {
        try {
            boolean result = adRoleService.createRole(adRoleDTO);
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
     * 更新角色
     */
    @PutMapping
    public BaseResponse<String> updateRole(@Valid @RequestBody AdRoleDTO adRoleDTO) {
        try {
            boolean result = adRoleService.updateRole(adRoleDTO);
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
     * 删除角色
     */
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteRole(@PathVariable String id) {
        try {
            boolean result = adRoleService.deleteRole(id);
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
     * 批量删除角色
     */
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
    @PutMapping("/{id}/permissions")
    public BaseResponse<String> assignPermissions(@PathVariable String id, @RequestBody List<String> permissionIds) {
        try {
            boolean result = adRoleService.assignPermissions(id, permissionIds);
            if (result) {
                return BaseResponse.success("权限分配成功");
            } else {
                return BaseResponse.error("权限分配失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据用户ID查询角色列表
     */
    @GetMapping("/user/{userId}")
    public BaseResponse<List<AdRole>> findByUserId(@PathVariable String userId) {
        List<AdRole> adRoles = adRoleService.findByUserId(userId);
        return BaseResponse.success(adRoles);
    }
}
