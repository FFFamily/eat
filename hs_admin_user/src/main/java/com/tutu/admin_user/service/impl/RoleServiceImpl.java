package com.tutu.admin_user.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.RoleDTO;
import com.tutu.admin_user.entity.Role;
import com.tutu.admin_user.entity.RolePermission;
import com.tutu.admin_user.mapper.RoleMapper;
import com.tutu.admin_user.mapper.RolePermissionMapper;
import com.tutu.admin_user.service.RoleService;
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
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Override
    public Role findByCode(String code) {
        return baseMapper.findByCode(code);
    }
    
    @Override
    public IPage<Role> getPageList(int current, int size, String keyword) {
        Page<Role> page = new Page<>(current, size);
        return baseMapper.selectPageByKeyword(page, keyword);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(RoleDTO roleDTO) {
        // 检查角色编码是否已存在
        if (findByCode(roleDTO.getCode()) != null) {
            throw new RuntimeException("角色编码已存在");
        }
        
        Role role = new Role();
        BeanUtils.copyProperties(roleDTO, role);
        
        boolean result = save(role);
        
        // 分配权限
        if (result && roleDTO.getPermissionIds() != null && !roleDTO.getPermissionIds().isEmpty()) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(RoleDTO roleDTO) {
        Role role = getById(roleDTO.getId());
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }
        
        // 检查角色编码是否被其他角色使用
        Role existRole = findByCode(roleDTO.getCode());
        if (existRole != null && !existRole.getId().equals(roleDTO.getId())) {
            throw new RuntimeException("角色编码已被使用");
        }
        
        BeanUtils.copyProperties(roleDTO, role);
        
        boolean result = updateById(role);
        
        // 重新分配权限
        if (result && roleDTO.getPermissionIds() != null) {
            assignPermissions(role.getId(), roleDTO.getPermissionIds());
        }
        
        return result;
    }
    
    @Override
    public boolean deleteRole(String id) {
        return removeById(id);
    }
    
    @Override
    public boolean batchDeleteRoles(List<String> ids) {
        return removeByIds(ids);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(String roleId, List<String> permissionIds) {
        // 先删除原有权限关联
        rolePermissionMapper.deleteByRoleId(roleId);
        
        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<RolePermission> rolePermissions = new ArrayList<>();
            for (String permissionId : permissionIds) {
                RolePermission rolePermission = new RolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreateTime(new Date());
                rolePermission.setUpdateTime(new Date());
                rolePermissions.add(rolePermission);
            }
            rolePermissionMapper.batchInsert(rolePermissions);
        }
        
        return true;
    }
    
    @Override
    public List<Role> findByUserId(String userId) {
        return baseMapper.findByUserId(userId);
    }
    
    @Override
    public List<Role> findAllEnabled() {
        return baseMapper.findAllEnabled();
    }
}
