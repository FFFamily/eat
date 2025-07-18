package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色Mapper接口
 */
@Mapper
public interface AdRoleMapper extends BaseMapper<AdRole> {
    
    /**
     * 根据角色编码查询角色
     */
    @Select("SELECT * FROM role WHERE code = #{code} AND is_deleted = '0'")
    AdRole findByCode(@Param("code") String code);
    
    /**
     * 分页查询角色列表
     */
    @Select("SELECT * FROM role WHERE is_deleted = '0' " +
            "AND (#{keyword} IS NULL OR name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR code LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY create_time DESC")
    IPage<AdRole> selectPageByKeyword(Page<AdRole> page, @Param("keyword") String keyword);
    
    /**
     * 根据用户ID查询角色列表
     */
    @Select("SELECT r.* FROM role r " +
            "INNER JOIN admin_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.is_deleted = '0' AND ur.is_deleted = '0'")
    List<AdRole> findByUserId(@Param("userId") String userId);
    
    /**
     * 查询所有启用的角色
     */
    @Select("SELECT * FROM role WHERE is_deleted = '0' ORDER BY create_time DESC")
    List<AdRole> findAllEnabled();
}
