package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.admin_user.entity.RolePermission;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色权限关联Mapper接口
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    
    /**
     * 根据角色ID查询角色权限关联
     */
    @Select("SELECT * FROM role_permission WHERE role_id = #{roleId} AND is_deleted = '0'")
    List<RolePermission> findByRoleId(@Param("roleId") String roleId);
    
    /**
     * 根据权限ID查询角色权限关联
     */
    @Select("SELECT * FROM role_permission WHERE permission_id = #{permissionId} AND is_deleted = '0'")
    List<RolePermission> findByPermissionId(@Param("permissionId") String permissionId);
    
    /**
     * 根据角色ID删除角色权限关联（逻辑删除）
     */
    @Delete("UPDATE role_permission SET is_deleted = '1' WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") String roleId);
    
    /**
     * 根据权限ID删除角色权限关联（逻辑删除）
     */
    @Delete("UPDATE role_permission SET is_deleted = '1' WHERE permission_id = #{permissionId}")
    int deleteByPermissionId(@Param("permissionId") String permissionId);
    
    /**
     * 批量插入角色权限关联
     */
    int batchInsert(@Param("rolePermissions") List<RolePermission> rolePermissions);
}
