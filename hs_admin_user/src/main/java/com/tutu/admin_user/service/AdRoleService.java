package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdRoleDTO;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.entity.AdRolePermission;
import com.tutu.admin_user.mapper.AdRoleMapper;
import com.tutu.admin_user.mapper.AdRolePermissionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 角色服务实现类
 */
@Service
public class AdRoleService extends ServiceImpl<AdRoleMapper, AdRole> {
    
    @Autowired
    private AdRolePermissionMapper adRolePermissionMapper;
    
    
    public AdRole findByCode(String code) {
        return baseMapper.findByCode(code);
    }
    
    
    public IPage<AdRole> getPageList(int current, int size, String keyword) {
        Page<AdRole> page = new Page<>(current, size);
        return baseMapper.selectPageByKeyword(page, keyword);
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(AdRoleDTO adRoleDTO) {
        // 检查角色编码是否已存在
        if (findByCode(adRoleDTO.getCode()) != null) {
            throw new RuntimeException("角色编码已存在");
        }
        
        AdRole adRole = new AdRole();
        BeanUtils.copyProperties(adRoleDTO, adRole);
        
        boolean result = save(adRole);
        
        // 分配权限
        if (result && adRoleDTO.getPermissionIds() != null && !adRoleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(adRole.getId(), adRoleDTO.getPermissionIds());
        }
        
        return result;
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(AdRoleDTO adRoleDTO) {
        AdRole adRole = getById(adRoleDTO.getId());
        if (adRole == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 检查角色编码是否被其他角色使用
        AdRole existAdRole = findByCode(adRoleDTO.getCode());
        if (existAdRole != null && !existAdRole.getId().equals(adRoleDTO.getId())) {
            throw new RuntimeException("角色编码已被使用");
        }
        
        BeanUtils.copyProperties(adRoleDTO, adRole);
        
        boolean result = updateById(adRole);
        
        // 重新分配权限
        if (result && adRoleDTO.getPermissionIds() != null) {
            assignPermissions(adRole.getId(), adRoleDTO.getPermissionIds());
        }
        
        return result;
    }
    
    
    public boolean deleteRole(String id) {
        return removeById(id);
    }
    
    
    public boolean batchDeleteRoles(List<String> ids) {
        return removeByIds(ids);
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(String roleId, List<String> permissionIds) {
        // 先删除原有权限关联
        adRolePermissionMapper.deleteByRoleId(roleId);
        
        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<AdRolePermission> adRolePermissions = new ArrayList<>();
            for (String permissionId : permissionIds) {
                AdRolePermission adRolePermission = new AdRolePermission();
                adRolePermission.setRoleId(roleId);
                adRolePermission.setPermissionId(permissionId);
                adRolePermission.setCreateTime(new Date());
                adRolePermission.setUpdateTime(new Date());
                adRolePermissions.add(adRolePermission);
            }
            adRolePermissionMapper.batchInsert(adRolePermissions);
        }
        
        return true;
    }
    
    
    public List<AdRole> findByUserId(String userId) {
        return baseMapper.findByUserId(userId);
    }
    
    
    public List<AdRole> findAllEnabled() {
        return baseMapper.findAllEnabled();
    }
}
