package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tutu.admin_user.dto.DepartmentDTO;
import com.tutu.admin_user.entity.Department;

import java.util.List;

/**
 * 部门服务接口
 */
public interface DepartmentService extends IService<Department> {
    
    /**
     * 根据部门编码查询部门
     */
    Department findByCode(String code);
    
    /**
     * 分页查询部门列表
     */
    IPage<Department> getPageList(int current, int size, String keyword);
    
    /**
     * 创建部门
     */
    boolean createDepartment(DepartmentDTO departmentDTO);
    
    /**
     * 更新部门
     */
    boolean updateDepartment(DepartmentDTO departmentDTO);
    
    /**
     * 删除部门
     */
    boolean deleteDepartment(String id);
    
    /**
     * 批量删除部门
     */
    boolean batchDeleteDepartments(List<String> ids);
    
    /**
     * 构建部门树
     */
    List<Department> buildDepartmentTree(List<Department> departments);
    
    /**
     * 查询部门树
     */
    List<Department> getDepartmentTree();
    
    /**
     * 查询所有启用的部门
     */
    List<Department> findAllEnabled();
}
