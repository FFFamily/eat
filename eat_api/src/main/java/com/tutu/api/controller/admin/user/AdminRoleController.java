package com.tutu.api.controller.admin.user;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tutu.common.Response.BaseResponse;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import com.tutu.user.entity.UserRole;
import com.tutu.user.service.UserRoleService;
import java.util.List;

@RestController
@RequestMapping("/admin/role")
public class AdminRoleController {

    @Resource
    private UserRoleService userRoleService;

    /**
     * 给用户分配角色
     * POST /admin/role/assign
     */
    @PostMapping("/assign")
    public BaseResponse<UserRole> assignRoleToUser(@RequestBody UserRole userRole) {
        userRoleService.save(userRole);
        return BaseResponse.success(userRole);
    }

    /**
     * 更新用户角色分配
     * PUT /admin/role/assign/{id}
     */
    @PutMapping("/assign/{id}")
    public BaseResponse<UserRole> updateRoleAssignment(@PathVariable("id") String id, @RequestBody UserRole userRole) {
        UserRole existingUserRole = userRoleService.getById(id);
        if (existingUserRole == null) {
            return new BaseResponse<>(404, "用户角色分配记录不存在", null);
        }
        BeanUtils.copyProperties(userRole, existingUserRole);
        userRoleService.save(existingUserRole);
        return new BaseResponse<>(200, "更新成功", existingUserRole);
    }

    /**
     * 删除用户角色分配
     * DELETE /admin/role/assign/{id}
     */
    @DeleteMapping("/assign/{id}")
    public BaseResponse<Void> deleteRoleAssignment(@PathVariable("id") String id) {
        UserRole existingUserRole = userRoleService.getById(id);
        if (existingUserRole == null) {
            return new BaseResponse<>(404, "用户角色分配记录不存在", null);
        }
        userRoleService.removeById(id);
        return new BaseResponse<>(200, "删除成功", null);
    }

    /**
     * 根据用户ID查询用户角色分配
     * GET /admin/role/assign/user/{userId}
     */
    @GetMapping("/assign/user/{userId}")
    public BaseResponse<List<UserRole>> getRoleAssignmentsByUserId(@PathVariable("userId") String userId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        List<UserRole> userRoles = userRoleService.list(queryWrapper);
        return BaseResponse.success(userRoles);
    }
}
