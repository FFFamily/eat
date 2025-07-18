package com.tutu.admin_user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.PermissionDTO;
import com.tutu.admin_user.entity.Permission;
import com.tutu.admin_user.mapper.PermissionMapper;
import com.tutu.admin_user.service.PermissionService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    
    @Override
    public Permission findByCode(String code) {
        return baseMapper.findByCode(code);
    }
    
    @Override
    public IPage<Permission> getPageList(int current, int size, String keyword) {
        Page<Permission> page = new Page<>(current, size);
        return baseMapper.selectPageByKeyword(page, keyword);
    }
    
    @Override
    public boolean createPermission(PermissionDTO permissionDTO) {
        // 检查权限编码是否已存在
        if (findByCode(permissionDTO.getCode()) != null) {
            throw new RuntimeException("权限编码已存在");
        }
        
        Permission permission = new Permission();
        BeanUtils.copyProperties(permissionDTO, permission);
        
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
    
    @Override
    public boolean updatePermission(PermissionDTO permissionDTO) {
        Permission permission = getById(permissionDTO.getId());
        if (permission == null) {
            throw new RuntimeException("权限不存在");
        }
        
        // 检查权限编码是否被其他权限使用
        Permission existPermission = findByCode(permissionDTO.getCode());
        if (existPermission != null && !existPermission.getId().equals(permissionDTO.getId())) {
            throw new RuntimeException("权限编码已被使用");
        }
        
        BeanUtils.copyProperties(permissionDTO, permission);
        
        return updateById(permission);
    }
    
    @Override
    public boolean deletePermission(String id) {
        // 检查是否有子权限
        List<Permission> children = baseMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子权限，无法删除");
        }
        
        return removeById(id);
    }
    
    @Override
    public boolean batchDeletePermissions(List<String> ids) {
        // 检查每个权限是否有子权限
        for (String id : ids) {
            List<Permission> children = baseMapper.findByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("存在子权限，无法删除");
            }
        }
        
        return removeByIds(ids);
    }
    
    @Override
    public List<Permission> findByRoleId(String roleId) {
        return baseMapper.findByRoleId(roleId);
    }
    
    @Override
    public List<Permission> findByUserId(String userId) {
        return baseMapper.findByUserId(userId);
    }
    
    @Override
    public List<Permission> buildPermissionTree(List<Permission> permissions) {
        List<Permission> tree = new ArrayList<>();
        
        // 找出根节点
        List<Permission> rootNodes = permissions.stream()
                .filter(permission -> "0".equals(permission.getParentId()) || permission.getParentId() == null)
                .collect(Collectors.toList());
        
        // 为每个根节点构建子树
        for (Permission root : rootNodes) {
            buildChildren(root, permissions);
            tree.add(root);
        }
        
        return tree;
    }
    
    /**
     * 递归构建子节点
     */
    private void buildChildren(Permission parent, List<Permission> allPermissions) {
        List<Permission> children = allPermissions.stream()
                .filter(permission -> parent.getId().equals(permission.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Permission child : children) {
                buildChildren(child, allPermissions);
            }
        }
    }
    
    @Override
    public List<Permission> getMenuTree() {
        List<Permission> allMenus = baseMapper.findAllMenus();
        return buildPermissionTree(allMenus);
    }
}
