package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdPermissionDTO;
import com.tutu.admin_user.entity.AdPermission;
import com.tutu.admin_user.mapper.AdPermissionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限服务实现类
 */
@Service
public class AdPermissionService extends ServiceImpl<AdPermissionMapper, AdPermission>  {
    
    
    public AdPermission findByCode(String code) {
        return baseMapper.findByCode(code);
    }
    
    
    public IPage<AdPermission> getPageList(int current, int size, String keyword) {
        Page<AdPermission> page = new Page<>(current, size);
        return baseMapper.selectPageByKeyword(page, keyword);
    }
    
    
    public boolean createPermission(AdPermissionDTO adPermissionDTO) {
        // 检查权限编码是否已存在
        if (findByCode(adPermissionDTO.getCode()) != null) {
            throw new RuntimeException("权限编码已存在");
        }
        
        AdPermission adPermission = new AdPermission();
        BeanUtils.copyProperties(adPermissionDTO, adPermission);
        
        // 设置默认状态
        if (adPermission.getStatus() == null) {
            adPermission.setStatus(1);
        }
        
        // 设置默认排序
        if (adPermission.getSortOrder() == null) {
            adPermission.setSortOrder(0);
        }
        
        return save(adPermission);
    }
    
    
    public boolean updatePermission(AdPermissionDTO adPermissionDTO) {
        AdPermission adPermission = getById(adPermissionDTO.getId());
        if (adPermission == null) {
            throw new RuntimeException("权限不存在");
        }
        
        // 检查权限编码是否被其他权限使用
        AdPermission existAdPermission = findByCode(adPermissionDTO.getCode());
        if (existAdPermission != null && !existAdPermission.getId().equals(adPermissionDTO.getId())) {
            throw new RuntimeException("权限编码已被使用");
        }
        
        BeanUtils.copyProperties(adPermissionDTO, adPermission);
        
        return updateById(adPermission);
    }
    
    
    public boolean deletePermission(String id) {
        // 检查是否有子权限
        List<AdPermission> children = baseMapper.findByParentId(id);
        if (!children.isEmpty()) {
            throw new RuntimeException("存在子权限，无法删除");
        }
        
        return removeById(id);
    }
    
    
    public boolean batchDeletePermissions(List<String> ids) {
        // 检查每个权限是否有子权限
        for (String id : ids) {
            List<AdPermission> children = baseMapper.findByParentId(id);
            if (!children.isEmpty()) {
                throw new RuntimeException("存在子权限，无法删除");
            }
        }
        
        return removeByIds(ids);
    }
    
    
    public List<AdPermission> findByRoleId(String roleId) {
        return baseMapper.findByRoleId(roleId);
    }
    
    
    public List<AdPermission> findByUserId(String userId) {
        return baseMapper.findByUserId(userId);
    }
    
    
    public List<AdPermission> buildPermissionTree(List<AdPermission> adPermissions) {
        List<AdPermission> tree = new ArrayList<>();
        
        // 找出根节点
        List<AdPermission> rootNodes = adPermissions.stream()
                .filter(permission -> "0".equals(permission.getParentId()) || permission.getParentId() == null)
                .collect(Collectors.toList());
        
        // 为每个根节点构建子树
        for (AdPermission root : rootNodes) {
            buildChildren(root, adPermissions);
            tree.add(root);
        }
        
        return tree;
    }
    
    /**
     * 递归构建子节点
     */
    private void buildChildren(AdPermission parent, List<AdPermission> allAdPermissions) {
        List<AdPermission> children = allAdPermissions.stream()
                .filter(permission -> parent.getId().equals(permission.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (AdPermission child : children) {
                buildChildren(child, allAdPermissions);
            }
        }
    }
    
    
    public List<AdPermission> getMenuTree() {
        List<AdPermission> allMenus = baseMapper.findAllMenus();
        return buildPermissionTree(allMenus);
    }
}
