package com.tutu.admin_user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.DepartmentDTO;
import com.tutu.admin_user.entity.Department;
import com.tutu.admin_user.mapper.DepartmentMapper;
import com.tutu.admin_user.service.DepartmentService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department> implements DepartmentService {
    
    @Override
    public Department findByCode(String code) {
        return baseMapper.findByCode(code);
    }
    
    @Override
    public IPage<Department> getPageList(int current, int size, String keyword) {
        Page<Department> page = new Page<>(current, size);
        return baseMapper.selectPageWithParent(page, keyword);
    }
    
    @Override
    public boolean createDepartment(DepartmentDTO departmentDTO) {
        // 检查部门编码是否已存在
        if (findByCode(departmentDTO.getCode()) != null) {
            throw new RuntimeException("部门编码已存在");
        }
        
        Department department = new Department();
        BeanUtils.copyProperties(departmentDTO, department);
        
        // 设置默认状态
        if (department.getStatus() == null) {
            department.setStatus(1);
        }
        
        // 设置默认排序
        if (department.getSortOrder() == null) {
            department.setSortOrder(0);
        }
        
        // 设置默认父部门ID
        if (department.getParentId() == null) {
            department.setParentId("0");
        }
        
        return save(department);
    }
    
    @Override
    public boolean updateDepartment(DepartmentDTO departmentDTO) {
        Department department = getById(departmentDTO.getId());
        if (department == null) {
            throw new RuntimeException("部门不存在");
        }
        
        // 检查部门编码是否被其他部门使用
        Department existDepartment = findByCode(departmentDTO.getCode());
        if (existDepartment != null && !existDepartment.getId().equals(departmentDTO.getId())) {
            throw new RuntimeException("部门编码已被使用");
        }
        
        // 检查是否设置自己为父部门
        if (departmentDTO.getId().equals(departmentDTO.getParentId())) {
            throw new RuntimeException("不能设置自己为父部门");
        }
        
        BeanUtils.copyProperties(departmentDTO, department);
        
        return updateById(department);
    }
    
    @Override
    public boolean deleteDepartment(String id) {
        // 检查是否有子部门
        List<Department> children = baseMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子部门，无法删除");
        }
        
        return removeById(id);
    }
    
    @Override
    public boolean batchDeleteDepartments(List<String> ids) {
        // 检查每个部门是否有子部门
        for (String id : ids) {
            List<Department> children = baseMapper.findByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("存在子部门，无法删除");
            }
        }
        
        return removeByIds(ids);
    }
    
    @Override
    public List<Department> buildDepartmentTree(List<Department> departments) {
        List<Department> tree = new ArrayList<>();
        
        // 找出根节点
        List<Department> rootNodes = departments.stream()
                .filter(department -> "0".equals(department.getParentId()) || department.getParentId() == null)
                .collect(Collectors.toList());
        
        // 为每个根节点构建子树
        for (Department root : rootNodes) {
            buildChildren(root, departments);
            tree.add(root);
        }
        
        return tree;
    }
    
    /**
     * 递归构建子节点
     */
    private void buildChildren(Department parent, List<Department> allDepartments) {
        List<Department> children = allDepartments.stream()
                .filter(department -> parent.getId().equals(department.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Department child : children) {
                buildChildren(child, allDepartments);
            }
        }
    }
    
    @Override
    public List<Department> getDepartmentTree() {
        List<Department> allDepartments = baseMapper.findAllDepartments();
        return buildDepartmentTree(allDepartments);
    }
    
    @Override
    public List<Department> findAllEnabled() {
        return baseMapper.findAllEnabled();
    }
}
