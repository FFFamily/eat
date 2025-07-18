package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdDepartmentDTO;
import com.tutu.admin_user.entity.AdDepartment;
import com.tutu.admin_user.mapper.AdDepartmentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 */
@Service
public class AdDepartmentService extends ServiceImpl<AdDepartmentMapper, AdDepartment>  {
    
    
    public AdDepartment findByCode(String code) {
        return baseMapper.findByCode(code);
    }
    
    
    public IPage<AdDepartment> getPageList(int current, int size, String keyword) {
        Page<AdDepartment> page = new Page<>(current, size);
        return baseMapper.selectPageWithParent(page, keyword);
    }
    
    
    public boolean createDepartment(AdDepartmentDTO adDepartmentDTO) {
        // 检查部门编码是否已存在
        if (findByCode(adDepartmentDTO.getCode()) != null) {
            throw new RuntimeException("部门编码已存在");
        }
        
        AdDepartment adDepartment = new AdDepartment();
        BeanUtils.copyProperties(adDepartmentDTO, adDepartment);
        
        // 设置默认状态
        if (adDepartment.getStatus() == null) {
            adDepartment.setStatus(1);
        }
        
        // 设置默认排序
        if (adDepartment.getSortOrder() == null) {
            adDepartment.setSortOrder(0);
        }
        
        // 设置默认父部门ID
        if (adDepartment.getParentId() == null) {
            adDepartment.setParentId("0");
        }
        
        return save(adDepartment);
    }
    
    
    public boolean updateDepartment(AdDepartmentDTO adDepartmentDTO) {
        AdDepartment adDepartment = getById(adDepartmentDTO.getId());
        if (adDepartment == null) {
            throw new RuntimeException("部门不存在");
        }
        
        // 检查部门编码是否被其他部门使用
        AdDepartment existAdDepartment = findByCode(adDepartmentDTO.getCode());
        if (existAdDepartment != null && !existAdDepartment.getId().equals(adDepartmentDTO.getId())) {
            throw new RuntimeException("部门编码已被使用");
        }
        
        // 检查是否设置自己为父部门
        if (adDepartmentDTO.getId().equals(adDepartmentDTO.getParentId())) {
            throw new RuntimeException("不能设置自己为父部门");
        }
        
        BeanUtils.copyProperties(adDepartmentDTO, adDepartment);
        
        return updateById(adDepartment);
    }
    
    
    public boolean deleteDepartment(String id) {
        // 检查是否有子部门
        List<AdDepartment> children = baseMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子部门，无法删除");
        }
        
        return removeById(id);
    }
    
    
    public boolean batchDeleteDepartments(List<String> ids) {
        // 检查每个部门是否有子部门
        for (String id : ids) {
            List<AdDepartment> children = baseMapper.findByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("存在子部门，无法删除");
            }
        }
        
        return removeByIds(ids);
    }
    
    
    public List<AdDepartment> buildDepartmentTree(List<AdDepartment> adDepartments) {
        List<AdDepartment> tree = new ArrayList<>();
        
        // 找出根节点
        List<AdDepartment> rootNodes = adDepartments.stream()
                .filter(department -> "0".equals(department.getParentId()) || department.getParentId() == null)
                .collect(Collectors.toList());
        
        // 为每个根节点构建子树
        for (AdDepartment root : rootNodes) {
            buildChildren(root, adDepartments);
            tree.add(root);
        }
        
        return tree;
    }
    
    /**
     * 递归构建子节点
     */
    private void buildChildren(AdDepartment parent, List<AdDepartment> allAdDepartments) {
        List<AdDepartment> children = allAdDepartments.stream()
                .filter(department -> parent.getId().equals(department.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (AdDepartment child : children) {
                buildChildren(child, allAdDepartments);
            }
        }
    }
    
    
    public List<AdDepartment> getDepartmentTree() {
        List<AdDepartment> allAdDepartments = baseMapper.findAllDepartments();
        return buildDepartmentTree(allAdDepartments);
    }
    
    
    public List<AdDepartment> findAllEnabled() {
        return baseMapper.findAllEnabled();
    }
}
