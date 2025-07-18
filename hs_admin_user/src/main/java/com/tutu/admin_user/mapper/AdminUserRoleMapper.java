package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.admin_user.entity.AdminUserRole;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员用户角色关联Mapper接口
 */
@Mapper
public interface AdminUserRoleMapper extends BaseMapper<AdminUserRole> {
    
    /**
     * 根据用户ID查询用户角色关联
     */
    @Select("SELECT * FROM admin_user_role WHERE user_id = #{userId} AND is_deleted = '0'")
    List<AdminUserRole> findByUserId(@Param("userId") String userId);
    
    /**
     * 根据角色ID查询用户角色关联
     */
    @Select("SELECT * FROM admin_user_role WHERE role_id = #{roleId} AND is_deleted = '0'")
    List<AdminUserRole> findByRoleId(@Param("roleId") String roleId);
    
    /**
     * 根据用户ID删除用户角色关联（逻辑删除）
     */
    @Delete("UPDATE admin_user_role SET is_deleted = '1' WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") String userId);
    
    /**
     * 根据角色ID删除用户角色关联（逻辑删除）
     */
    @Delete("UPDATE admin_user_role SET is_deleted = '1' WHERE role_id = #{roleId}")
    int deleteByRoleId(@Param("roleId") String roleId);
    
    /**
     * 批量插入用户角色关联
     */
    int batchInsert(@Param("userRoles") List<AdminUserRole> userRoles);
}
