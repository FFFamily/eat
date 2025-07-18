package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.admin_user.entity.AdDepartment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 部门Mapper接口
 */
@Mapper
public interface AdDepartmentMapper extends BaseMapper<AdDepartment> {
    
    /**
     * 根据部门编码查询部门
     */
    @Select("SELECT * FROM department WHERE code = #{code} AND is_deleted = '0'")
    AdDepartment findByCode(@Param("code") String code);
    
    /**
     * 分页查询部门列表
     */
    @Select("SELECT d.*, p.name as parent_name FROM department d " +
            "LEFT JOIN department p ON d.parent_id = p.id " +
            "WHERE d.is_deleted = '0' " +
            "AND (#{keyword} IS NULL OR d.name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR d.code LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY d.sort_order ASC, d.create_time DESC")
    IPage<AdDepartment> selectPageWithParent(Page<AdDepartment> page, @Param("keyword") String keyword);
    
    /**
     * 根据父部门ID查询子部门列表
     */
    @Select("SELECT * FROM department WHERE parent_id = #{parentId} AND is_deleted = '0' " +
            "ORDER BY sort_order ASC")
    List<AdDepartment> findByParentId(@Param("parentId") String parentId);
    
    /**
     * 查询所有部门（树形结构）
     */
    @Select("SELECT * FROM department WHERE is_deleted = '0' ORDER BY sort_order ASC")
    List<AdDepartment> findAllDepartments();
    
    /**
     * 查询所有启用的部门
     */
    @Select("SELECT * FROM department WHERE status = 1 AND is_deleted = '0' " +
            "ORDER BY sort_order ASC")
    List<AdDepartment> findAllEnabled();
}
