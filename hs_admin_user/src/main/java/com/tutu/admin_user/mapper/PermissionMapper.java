package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 权限Mapper接口
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    
    /**
     * 根据权限编码查询权限
     */
    @Select("SELECT * FROM permission WHERE code = #{code} AND is_deleted = '0'")
    Permission findByCode(@Param("code") String code);
    
    /**
     * 分页查询权限列表
     */
    @Select("SELECT * FROM permission WHERE is_deleted = '0' " +
            "AND (#{keyword} IS NULL OR name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR code LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY sort_order ASC, create_time DESC")
    IPage<Permission> selectPageByKeyword(Page<Permission> page, @Param("keyword") String keyword);
    
    /**
     * 根据角色ID查询权限列表
     */
    @Select("SELECT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "WHERE rp.role_id = #{roleId} AND p.is_deleted = '0' AND rp.is_deleted = '0' " +
            "ORDER BY p.sort_order ASC")
    List<Permission> findByRoleId(@Param("roleId") String roleId);
    
    /**
     * 根据用户ID查询权限列表
     */
    @Select("SELECT DISTINCT p.* FROM permission p " +
            "INNER JOIN role_permission rp ON p.id = rp.permission_id " +
            "INNER JOIN admin_user_role ur ON rp.role_id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND p.is_deleted = '0' " +
            "AND rp.is_deleted = '0' AND ur.is_deleted = '0' " +
            "ORDER BY p.sort_order ASC")
    List<Permission> findByUserId(@Param("userId") String userId);
    
    /**
     * 根据父权限ID查询子权限列表
     */
    @Select("SELECT * FROM permission WHERE parent_id = #{parentId} AND is_deleted = '0' " +
            "ORDER BY sort_order ASC")
    List<Permission> findByParentId(@Param("parentId") String parentId);
    
    /**
     * 查询所有菜单权限（树形结构）
     */
    @Select("SELECT * FROM permission WHERE type = 1 AND is_deleted = '0' " +
            "ORDER BY sort_order ASC")
    List<Permission> findAllMenus();
}
