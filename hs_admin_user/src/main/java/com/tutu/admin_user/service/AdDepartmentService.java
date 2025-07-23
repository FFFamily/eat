package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdDepartmentDTO;
import com.tutu.admin_user.entity.AdDepartment;
import com.tutu.admin_user.mapper.AdDepartmentMapper;
import com.tutu.common.constant.CommonConstant;
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
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdDepartment::getCode, code)
                .eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR);
        return getOne(queryWrapper);
    }

    
    public IPage<AdDepartment> getPageList(int current, int size, String keyword) {
        Page<AdDepartment> page = new Page<>(current, size);
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AdDepartment::getName, keyword)
                    .or()
                    .like(AdDepartment::getCode, keyword)
            );
        }

        queryWrapper.orderByAsc(AdDepartment::getSortOrder)
                .orderByDesc(AdDepartment::getCreateTime);

        return page(page, queryWrapper);
    }

    
    public boolean createDepartment(AdDepartment department) {
        // 检查部门编码是否已存在
        if (findByCode(department.getCode()) != null) {
            throw new RuntimeException("部门编码已存在");
        }

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

    
    public boolean updateDepartment(AdDepartment department) {
        AdDepartment existDepartment = getById(department.getId());
        if (existDepartment == null) {
            throw new RuntimeException("部门不存在");
        }

        // 检查部门编码是否被其他部门使用
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdDepartment::getCode, department.getCode())
                .eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR)
                .ne(AdDepartment::getId, department.getId());

        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("部门编码已被使用");
        }

        // 检查是否设置自己为父部门
        if (department.getId().equals(department.getParentId())) {
            throw new RuntimeException("不能设置自己为父部门");
        }

        return updateById(department);
    }

    
    public boolean deleteDepartment(String id) {
        // 检查是否有子部门
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdDepartment::getParentId, id)
                .eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR);

        if (count(queryWrapper) > 0) {
            throw new RuntimeException("存在子部门，无法删除");
        }

        return removeById(id);
    }

    
    public List<AdDepartment> findByParentId(String parentId) {
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdDepartment::getParentId, parentId)
                .eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdDepartment::getSortOrder);
        return list(queryWrapper);
    }

    
    public List<AdDepartment> buildDepartmentTree(List<AdDepartment> departments) {
        List<AdDepartment> tree = new ArrayList<>();

        // 找出根节点
        List<AdDepartment> rootNodes = departments.stream()
                .filter(department -> "0".equals(department.getParentId()) || department.getParentId() == null)
                .collect(Collectors.toList());

        // 为每个根节点构建子树
        for (AdDepartment root : rootNodes) {
            buildChildren(root, departments);
            tree.add(root);
        }

        return tree;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(AdDepartment parent, List<AdDepartment> allDepartments) {
        List<AdDepartment> children = allDepartments.stream()
                .filter(department -> parent.getId().equals(department.getParentId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (AdDepartment child : children) {
                buildChildren(child, allDepartments);
            }
        }
    }

    
    public List<AdDepartment> getDepartmentTree() {
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdDepartment::getSortOrder);

        List<AdDepartment> allDepartments = list(queryWrapper);
        return buildDepartmentTree(allDepartments);
    }

    
    public List<AdDepartment> findAllEnabled() {
        LambdaQueryWrapper<AdDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdDepartment::getStatus, 1)
                .eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdDepartment::getSortOrder);
        return list(queryWrapper);
    }

    public boolean batchDeleteDepartments(List<String> ids) {
        return removeByIds(ids);
    }
}
