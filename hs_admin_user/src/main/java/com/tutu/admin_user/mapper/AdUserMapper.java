package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 管理员用户Mapper接口
 */
@Mapper
public interface AdUserMapper extends BaseMapper<AdUser> {
    
    /**
     * 根据用户名查询用户
     */
    @Select("SELECT * FROM admin_user WHERE username = #{username} AND is_deleted = '0'")
    AdUser findByUsername(@Param("username") String username);
    
    /**
     * 分页查询用户列表（包含部门信息）
     */
    @Select("SELECT u.*, d.name as dept_name FROM admin_user u " +
            "LEFT JOIN department d ON u.dept_id = d.id " +
            "WHERE u.is_deleted = '0' " +
            "AND (#{keyword} IS NULL OR u.username LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR u.email LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY u.create_time DESC")
    IPage<AdUser> selectPageWithDept(Page<AdUser> page, @Param("keyword") String keyword);
    
    /**
     * 根据部门ID查询用户列表
     */
    @Select("SELECT * FROM admin_user WHERE dept_id = #{deptId} AND is_deleted = '0'")
    List<AdUser> findByDeptId(@Param("deptId") String deptId);
    
    /**
     * 根据角色ID查询用户列表
     */
    @Select("SELECT u.* FROM admin_user u " +
            "INNER JOIN admin_user_role ur ON u.id = ur.user_id " +
            "WHERE ur.role_id = #{roleId} AND u.is_deleted = '0' AND ur.is_deleted = '0'")
    List<AdUser> findByRoleId(@Param("roleId") String roleId);
}
