package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutu.admin_user.dto.RoleDTO;
import com.tutu.admin_user.entity.Role;

import java.util.List;

/**
 * 角色服务接口
 */
public interface RoleService extends IService<Role> {
    
    /**
     * 根据角色编码查询角色
     */
    Role findByCode(String code);
    
    /**
     * 分页查询角色列表
     */
    IPage<Role> getPageList(int current, int size, String keyword);
    
    /**
     * 创建角色
     */
    boolean createRole(RoleDTO roleDTO);
    
    /**
     * 更新角色
     */
    boolean updateRole(RoleDTO roleDTO);
    
    /**
     * 删除角色
     */
    boolean deleteRole(String id);
    
    /**
     * 批量删除角色
     */
    boolean batchDeleteRoles(List<String> ids);
    
    /**
     * 分配权限
     */
    boolean assignPermissions(String roleId, List<String> permissionIds);
    
    /**
     * 根据用户ID查询角色列表
     */
    List<Role> findByUserId(String userId);
    
    /**
     * 查询所有启用的角色
     */
    List<Role> findAllEnabled();
}
