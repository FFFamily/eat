package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.service.AdRoleService;
import com.tutu.admin_user.service.AdUserService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.AuditLog;
import com.tutu.common.annotation.PermissionRequired;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户控制器
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    
    @Resource
    private AdUserService adUserService;
    @Resource
    private AdRoleService adRoleService;
    
    /**
     * 分页查询用户列表
     */
    @PermissionRequired("user:list")
    @GetMapping("/page")
    public BaseResponse<IPage<AdUser>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deptId) {
        IPage<AdUser> page = adUserService.getPageList(current, size, keyword, status, deptId);
        page.getRecords().forEach(adUser -> adUser.setPassword(null));
        return BaseResponse.success(page);
    }
    
    /**
     * 根据ID查询用户详情
     */
    @PermissionRequired("user:read")
    @GetMapping("/info/{id}")
    public BaseResponse<AdUser> getById(@PathVariable String id) {
        AdUser user = adUserService.getById(id);
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        user.setAdRoles(adRoleService.findByUserId(id));
        user.setPassword(null);
        return BaseResponse.success(user);
    }
    
    /**
     * 创建用户
     */
    @PermissionRequired("user:create")
    @AuditLog(action = "user.create", targetType = "user", recordParams = false)
    @PostMapping("/create")
    public BaseResponse<String> createUser(@Valid @RequestBody AdUser userDTO) {
        try {
            boolean result = adUserService.createUser(userDTO);
            if (result) {
                // 支持一次性带角色创建（不破坏兼容：未传则跳过）
                if (userDTO.getAdRoles() != null && !userDTO.getAdRoles().isEmpty()) {
                    List<String> roleIds = userDTO.getAdRoles().stream()
                            .map(r -> r == null ? null : r.getId())
                            .filter(id -> id != null && !id.isBlank())
                            .toList();
                    if (!roleIds.isEmpty()) {
                        adUserService.assignRoles(userDTO.getId(), roleIds);
                    }
                }
                return BaseResponse.success("创建成功");
            } else {
                return BaseResponse.error("创建失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 更新用户
     */
    @PermissionRequired("user:update")
    @AuditLog(action = "user.update", targetType = "user")
    @PutMapping("/update")
    public BaseResponse<String> updateUser(@Valid @RequestBody AdUser userDTO) {
        adUserService.updateUser(userDTO);
        // 支持一次性带角色更新
        if (userDTO.getAdRoles() != null) {
            List<String> roleIds = userDTO.getAdRoles().stream()
                    .map(r -> r == null ? null : r.getId())
                    .filter(id -> id != null && !id.isBlank())
                    .toList();
            adUserService.assignRoles(userDTO.getId(), roleIds);
        }
        return BaseResponse.success();
    }
    
    /**
     * 删除用户
     */
    @PermissionRequired("user:delete")
    @AuditLog(action = "user.delete", targetType = "user")
    @DeleteMapping("/delete/{id}")
    public BaseResponse<String> deleteUser(@PathVariable String id) {
        try {
            boolean result = adUserService.deleteUser(id);
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
     * 批量删除用户
     */
    @PermissionRequired("user:delete")
    @AuditLog(action = "user.batch_delete", targetType = "user")
    @DeleteMapping("/batch")
    public BaseResponse<String> batchDeleteUsers(@RequestBody List<String> ids) {
        try {
            boolean result = adUserService.batchDeleteUsers(ids);
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
     * 重置密码
     */
    @PermissionRequired("user:reset_password")
    @AuditLog(action = "user.reset_password", targetType = "user", recordParams = false)
    @PutMapping("/{id}/reset-password")
    public BaseResponse<String> resetPassword(@PathVariable String id, @RequestBody Map<String, String> params) {
        try {
            String newPassword = params.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                newPassword = "123456"; // 默认密码
            }
            boolean result = adUserService.resetPassword(id, newPassword);
            if (result) {
                return BaseResponse.success("密码重置成功");
            } else {
                return BaseResponse.error("密码重置失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 修改密码
     */
    @PutMapping("/{id}/change-password")
    public BaseResponse<String> changePassword(@PathVariable String id, @RequestBody Map<String, String> params) {
        try {
            String oldPassword = params.get("oldPassword");
            String newPassword = params.get("newPassword");
            boolean result = adUserService.changePassword(id, oldPassword, newPassword);
            if (result) {
                return BaseResponse.success("密码修改成功");
            } else {
                return BaseResponse.error("密码修改失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 启用/禁用用户
     */
    @PermissionRequired("user:update")
    @AuditLog(action = "user.change_status", targetType = "user")
    @PutMapping("/changeStatus")
    public BaseResponse<String> changeStatus(@RequestBody Map<String, String> params) {
        String status = params.get("status");
        String userId = params.get("userId");
        adUserService.changeStatus(userId, status);
        return BaseResponse.success();
    }
    
    /**
     * 分配角色
     */
    @PermissionRequired("user:update")
    @AuditLog(action = "user.bind_roles", targetType = "user")
    @PutMapping("/{id}/roles")
    public BaseResponse<String> assignRoles(@PathVariable String id, @RequestBody List<String> roleIds) {
        try {
            boolean result = adUserService.assignRoles(id, roleIds);
            if (result) {
                return BaseResponse.success("角色分配成功");
            } else {
                return BaseResponse.error("角色分配失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 根据部门ID查询用户列表
     */
    @PermissionRequired("user:list")
    @GetMapping("/dept/{deptId}")
    public BaseResponse<List<AdUser>> findByDeptId(@PathVariable String deptId) {
        List<AdUser> users = adUserService.findByDeptId(deptId);
        return BaseResponse.success(users);
    }
    
    /**
     * 根据角色ID查询用户列表
     */
    @PermissionRequired("user:list")
    @GetMapping("/role/{roleId}")
    public BaseResponse<List<AdUser>> findByRoleId(@PathVariable String roleId) {
        List<AdUser> users = adUserService.findByRoleId(roleId);
        return BaseResponse.success(users);
    }
}
