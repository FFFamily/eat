package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutu.admin_user.dto.AdminUserDTO;
import com.tutu.admin_user.entity.AdminUser;

import java.util.List;

/**
 * 管理员用户服务接口
 */
public interface AdminUserService extends IService<AdminUser> {
    
    /**
     * 根据用户名查询用户
     */
    AdminUser findByUsername(String username);
    
    /**
     * 分页查询用户列表
     */
    IPage<AdminUser> getPageList(int current, int size, String keyword);
    
    /**
     * 创建用户
     */
    boolean createUser(AdminUserDTO userDTO);
    
    /**
     * 更新用户
     */
    boolean updateUser(AdminUserDTO userDTO);
    
    /**
     * 删除用户
     */
    boolean deleteUser(String id);
    
    /**
     * 批量删除用户
     */
    boolean batchDeleteUsers(List<String> ids);
    
    /**
     * 重置密码
     */
    boolean resetPassword(String id, String newPassword);
    
    /**
     * 修改密码
     */
    boolean changePassword(String id, String oldPassword, String newPassword);
    
    /**
     * 启用/禁用用户
     */
    boolean changeStatus(String id, Integer status);
    
    /**
     * 分配角色
     */
    boolean assignRoles(String userId, List<String> roleIds);
    
    /**
     * 根据部门ID查询用户列表
     */
    List<AdminUser> findByDeptId(String deptId);
    
    /**
     * 根据角色ID查询用户列表
     */
    List<AdminUser> findByRoleId(String roleId);
}
