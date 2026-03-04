package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdPermissionDTO;
import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.entity.AdRolePermission;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.entity.AdUserRole;
import com.tutu.admin_user.mapper.AdPermissionMapper;
import com.tutu.admin_user.mapper.AdRolePermissionMapper;
import com.tutu.admin_user.mapper.AdUserRoleMapper;
import com.tutu.common.constant.CommonConstant;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class AdPermissionService extends ServiceImpl<AdPermissionMapper, AdPermission>  {

    @Autowired
    private AdRoleService adRoleService;

    @Autowired
    private AdRolePermissionMapper adRolePermissionMapper;

    @Autowired
    private AdUserRoleMapper adUserRoleMapper;

    
    public AdPermission findByCode(String code) {
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdPermission::getCode, code)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR);
        return getOne(queryWrapper);
    }

    
    public IPage<AdPermission> getPageList(int current, int size, String keyword, Integer type, Integer status) {
        Page<AdPermission> page = new Page<>(current, size);
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AdPermission::getIsDeleted, CommonConstant.NO_STR);

        if (type != null) {
            queryWrapper.eq(AdPermission::getType, type);
        }
        if (status != null) {
            queryWrapper.eq(AdPermission::getStatus, status);
        }

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AdPermission::getName, keyword)
                    .or()
                    .like(AdPermission::getCode, keyword)
            );
        }

        queryWrapper.orderByAsc(AdPermission::getSortOrder)
                .orderByDesc(AdPermission::getCreateTime);

        return page(page, queryWrapper);
    }

    
    public boolean createPermission(AdPermission permission) {
        // Normalize parentId: root = "0"
        if (StrUtil.isBlank(permission.getParentId())) {
            permission.setParentId("0");
        }
        // 检查权限编码是否已存在
        if (findByCode(permission.getCode()) != null) {
            throw new RuntimeException("权限编码已存在");
        }

        // 设置默认状态
        if (permission.getStatus() == null) {
            permission.setStatus(1);
        }

        // 设置默认排序
        if (permission.getSortOrder() == null) {
            permission.setSortOrder(0);
        }

        return save(permission);
    }

    
    public boolean updatePermission(AdPermission permission) {
        AdPermission existPermission = getById(permission.getId());
        if (existPermission == null) {
            throw new RuntimeException("权限不存在");
        }

        // Normalize parentId: root = "0"
        if (StrUtil.isBlank(permission.getParentId())) {
            permission.setParentId("0");
        }

        // 检查权限编码是否被其他权限使用
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdPermission::getCode, permission.getCode())
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .ne(AdPermission::getId, permission.getId());

        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("权限编码已被使用");
        }

        return updateById(permission);
    }

    
    public boolean deletePermission(String id) {
        // 检查是否有子权限
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdPermission::getParentId, id)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR);

        if (count(queryWrapper) > 0) {
            throw new RuntimeException("存在子权限，无法删除");
        }

        return removeById(id);
    }

    
    public List<AdPermission> findByRoleId(String roleId) {
        // 先查询角色权限关联表
        LambdaQueryWrapper<AdRolePermission> rolePermissionQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionQueryWrapper.eq(AdRolePermission::getRoleId, roleId)
                .eq(AdRolePermission::getIsDeleted, CommonConstant.NO_STR);
        List<AdRolePermission> rolePermissions = adRolePermissionMapper.selectList(rolePermissionQueryWrapper);

        if (rolePermissions.isEmpty()) {
            return List.of();
        }

        // 提取权限ID列表
        List<String> permissionIds = rolePermissions.stream()
                .map(AdRolePermission::getPermissionId)
                .toList();

        // 查询权限信息
        LambdaQueryWrapper<AdPermission> permissionQueryWrapper = new LambdaQueryWrapper<>();
        permissionQueryWrapper.in(AdPermission::getId, permissionIds)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdPermission::getSortOrder);

        return list(permissionQueryWrapper);
    }

    
    public List<AdPermission> findByUserId(String userId) {
        // 先查询用户角色关联表
        LambdaQueryWrapper<AdUserRole> userRoleQueryWrapper = new LambdaQueryWrapper<>();
        userRoleQueryWrapper.eq(AdUserRole::getUserId, userId)
                .eq(AdUserRole::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserRole> userRoles = adUserRoleMapper.selectList(userRoleQueryWrapper);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 提取角色ID列表
        List<String> roleIds = userRoles.stream()
                .map(AdUserRole::getRoleId)
                .toList();

        // 查询角色权限关联表
        LambdaQueryWrapper<AdRolePermission> rolePermissionQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionQueryWrapper.in(AdRolePermission::getRoleId, roleIds)
                .eq(AdRolePermission::getIsDeleted, CommonConstant.NO_STR);
        List<AdRolePermission> rolePermissions = adRolePermissionMapper.selectList(rolePermissionQueryWrapper);

        if (rolePermissions.isEmpty()) {
            return List.of();
        }

        // 提取权限ID列表（去重）
        List<String> permissionIds = rolePermissions.stream()
                .map(AdRolePermission::getPermissionId)
                .distinct()
                .toList();

        // 查询权限信息
        LambdaQueryWrapper<AdPermission> permissionQueryWrapper = new LambdaQueryWrapper<>();
        permissionQueryWrapper.in(AdPermission::getId, permissionIds)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .eq(AdPermission::getStatus, 1)
                .orderByAsc(AdPermission::getSortOrder);

        return list(permissionQueryWrapper);
    }

    /**
     * 平台侧使用：返回所有启用的权限点（全局字典表）。
     */
    public List<AdPermission> listAllEnabled() {
        LambdaQueryWrapper<AdPermission> qw = new LambdaQueryWrapper<>();
        qw.eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .eq(AdPermission::getStatus, 1)
                .orderByAsc(AdPermission::getSortOrder);
        return list(qw);
    }

    /**
     * 查询指定权限ID集合中“启用”的权限点。
     */
    public List<AdPermission> listByIdsEnabled(Collection<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        LambdaQueryWrapper<AdPermission> qw = new LambdaQueryWrapper<>();
        qw.in(AdPermission::getId, ids)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .eq(AdPermission::getStatus, 1)
                .orderByAsc(AdPermission::getSortOrder);
        return list(qw);
    }

    
    public List<AdPermission> findByParentId(String parentId) {
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdPermission::getParentId, parentId)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdPermission::getSortOrder);
        return list(queryWrapper);
    }

    
    public List<AdPermission> buildPermissionTree(List<AdPermission> permissions) {
        List<AdPermission> tree = new ArrayList<>();

        // 找出根节点
        List<AdPermission> rootNodes = permissions.stream()
                .filter(permission -> StrUtil.isBlank(permission.getParentId()) || "0".equals(permission.getParentId()))
                .collect(Collectors.toList());

        // 为每个根节点构建子树
        for (AdPermission root : rootNodes) {
            buildChildren(root, permissions);
            tree.add(root);
        }

        return tree;
    }

    /**
     * 递归构建子节点
     */
    private void buildChildren(AdPermission parent, List<AdPermission> allPermissions) {
        List<AdPermission> children = allPermissions.stream()
                .filter(permission -> parent.getId().equals(permission.getParentId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (AdPermission child : children) {
                buildChildren(child, allPermissions);
            }
        }
    }

    
    public List<AdPermission> getMenuTree() {
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdPermission::getType, 1) // 菜单类型
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdPermission::getSortOrder);

        List<AdPermission> allMenus = list(queryWrapper);
        return buildPermissionTree(allMenus);
    }

    /**
     * 返回全部权限点树（菜单 + 按钮/接口）。
     * 用于角色授权等场景；如果仅需要菜单树，请使用 {@link #getMenuTree()}。
     */
    public List<AdPermission> getPermissionTree() {
        LambdaQueryWrapper<AdPermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdPermission::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(AdPermission::getSortOrder);
        List<AdPermission> all = list(queryWrapper);
        return buildPermissionTree(all);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeletePermissions(List<String> ids) {
        // Block deletion if any selected node has children.
        LambdaQueryWrapper<AdPermission> childQw = new LambdaQueryWrapper<>();
        childQw.in(AdPermission::getParentId, ids)
                .eq(AdPermission::getIsDeleted, CommonConstant.NO_STR);
        if (count(childQw) > 0) {
            throw new RuntimeException("存在子权限，无法批量删除");
        }
        return removeByIds(ids);
    }
}
