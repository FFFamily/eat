package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutu.admin_user.dto.PermissionDTO;
import com.tutu.admin_user.entity.Permission;

import java.util.List;

/**
 * 权限服务接口
 */
public interface PermissionService extends IService<Permission> {
    
    /**
     * 根据权限编码查询权限
     */
    Permission findByCode(String code);
    
    /**
     * 分页查询权限列表
     */
    IPage<Permission> getPageList(int current, int size, String keyword);
    
    /**
     * 创建权限
     */
    boolean createPermission(PermissionDTO permissionDTO);
    
    /**
     * 更新权限
     */
    boolean updatePermission(PermissionDTO permissionDTO);
    
    /**
     * 删除权限
     */
    boolean deletePermission(String id);
    
    /**
     * 批量删除权限
     */
    boolean batchDeletePermissions(List<String> ids);
    
    /**
     * 根据角色ID查询权限列表
     */
    List<Permission> findByRoleId(String roleId);
    
    /**
     * 根据用户ID查询权限列表
     */
    List<Permission> findByUserId(String userId);
    
    /**
     * 构建权限树
     */
    List<Permission> buildPermissionTree(List<Permission> permissions);
    
    /**
     * 查询所有菜单权限（树形结构）
     */
    List<Permission> getMenuTree();
}
