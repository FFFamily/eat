package com.tutu.admin_user.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.admin_user.dto.AdminUserDTO;
import com.tutu.admin_user.entity.AdminUser;
import com.tutu.admin_user.service.AdminUserService;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.annotation.OperationLog;
import com.tutu.common.enums.OperationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 管理员用户控制器
 */
@RestController
@RequestMapping("/admin/user")
public class AdminUserController {
    
    @Autowired
    private AdminUserService adminUserService;
    
    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public BaseResponse<IPage<AdminUser>> getPageList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<AdminUser> page = adminUserService.getPageList(current, size, keyword);
        return BaseResponse.success(page);
    }
    
    /**
     * 根据ID查询用户详情
     */
    @GetMapping("/{id}")
    public BaseResponse<AdminUser> getById(@PathVariable String id) {
        AdminUser user = adminUserService.getById(id);
        if (user == null) {
            return BaseResponse.error("用户不存在");
        }
        return BaseResponse.success(user);
    }
    
    /**
     * 创建用户
     */
    @PostMapping
    public BaseResponse<String> createUser(@Valid @RequestBody AdminUserDTO userDTO) {
        try {
            boolean result = adminUserService.createUser(userDTO);
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
     * 更新用户
     */
    @PutMapping
    public BaseResponse<String> updateUser(@Valid @RequestBody AdminUserDTO userDTO) {
        try {
            boolean result = adminUserService.updateUser(userDTO);
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
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public BaseResponse<String> deleteUser(@PathVariable String id) {
        try {
            boolean result = adminUserService.deleteUser(id);
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
    @DeleteMapping("/batch")
    public BaseResponse<String> batchDeleteUsers(@RequestBody List<String> ids) {
        try {
            boolean result = adminUserService.batchDeleteUsers(ids);
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
    @PutMapping("/{id}/reset-password")
    public BaseResponse<String> resetPassword(@PathVariable String id, @RequestBody Map<String, String> params) {
        try {
            String newPassword = params.get("newPassword");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                newPassword = "123456"; // 默认密码
            }
            boolean result = adminUserService.resetPassword(id, newPassword);
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
            boolean result = adminUserService.changePassword(id, oldPassword, newPassword);
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
    @PutMapping("/{id}/status")
    public BaseResponse<String> changeStatus(@PathVariable String id, @RequestBody Map<String, Integer> params) {
        try {
            Integer status = params.get("status");
            boolean result = adminUserService.changeStatus(id, status);
            if (result) {
                return BaseResponse.success("状态修改成功");
            } else {
                return BaseResponse.error("状态修改失败");
            }
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 分配角色
     */
    @PutMapping("/{id}/roles")
    public BaseResponse<String> assignRoles(@PathVariable String id, @RequestBody List<String> roleIds) {
        try {
            boolean result = adminUserService.assignRoles(id, roleIds);
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
    @GetMapping("/dept/{deptId}")
    public BaseResponse<List<AdminUser>> findByDeptId(@PathVariable String deptId) {
        List<AdminUser> users = adminUserService.findByDeptId(deptId);
        return BaseResponse.success(users);
    }
    
    /**
     * 根据角色ID查询用户列表
     */
    @GetMapping("/role/{roleId}")
    public BaseResponse<List<AdminUser>> findByRoleId(@PathVariable String roleId) {
        List<AdminUser> users = adminUserService.findByRoleId(roleId);
        return BaseResponse.success(users);
    }
}
