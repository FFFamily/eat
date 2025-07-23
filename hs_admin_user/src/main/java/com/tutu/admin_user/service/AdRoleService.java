package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdRoleDTO;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.entity.AdRolePermission;
import com.tutu.admin_user.entity.AdUserRole;
import com.tutu.admin_user.mapper.AdRoleMapper;
import com.tutu.admin_user.mapper.AdRolePermissionMapper;
import com.tutu.admin_user.mapper.AdUserRoleMapper;
import com.tutu.common.constant.CommonConstant;
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

    @Autowired
    private AdUserRoleMapper adUserRoleMapper;

    
    public AdRole findByCode(String code) {
        LambdaQueryWrapper<AdRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdRole::getCode, code)
                .eq(AdRole::getIsDeleted, CommonConstant.NO_STR);
        return getOne(queryWrapper);
    }

    
    public IPage<AdRole> getPageList(int current, int size, String keyword) {
        Page<AdRole> page = new Page<>(current, size);
        LambdaQueryWrapper<AdRole> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AdRole::getIsDeleted, CommonConstant.NO_STR);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AdRole::getName, keyword)
                    .or()
                    .like(AdRole::getCode, keyword)
            );
        }

        queryWrapper.orderByDesc(AdRole::getCreateTime);

        return page(page, queryWrapper);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean createRole(AdRole role) {
        // 检查角色编码是否已存在
        if (findByCode(role.getCode()) != null) {
            throw new RuntimeException("角色编码已存在");
        }

        return save(role);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean updateRole(AdRole role) {
        AdRole existRole = getById(role.getId());
        if (existRole == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查角色编码是否被其他角色使用
        LambdaQueryWrapper<AdRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdRole::getCode, role.getCode())
                .eq(AdRole::getIsDeleted, CommonConstant.NO_STR)
                .ne(AdRole::getId, role.getId());

        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("角色编码已被使用");
        }

        return updateById(role);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean assignPermissions(String roleId, List<String> permissionIds) {
        // 先删除原有权限关联
        LambdaUpdateWrapper<AdRolePermission> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(AdRolePermission::getRoleId, roleId)
                .set(AdRolePermission::getIsDeleted, CommonConstant.YES_STR)
                .set(AdRolePermission::getUpdateTime, new Date());
        adRolePermissionMapper.update(null, deleteWrapper);

        // 添加新的权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            for (String permissionId : permissionIds) {
                AdRolePermission rolePermission = new AdRolePermission();
                rolePermission.setRoleId(roleId);
                rolePermission.setPermissionId(permissionId);
                rolePermission.setCreateTime(new Date());
                rolePermission.setUpdateTime(new Date());
                rolePermission.setIsDeleted(CommonConstant.NO_STR);
                adRolePermissionMapper.insert(rolePermission);
            }
        }

        return true;
    }

    
    public List<AdRole> findByUserId(String userId) {
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

        // 查询角色信息
        LambdaQueryWrapper<AdRole> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.in(AdRole::getId, roleIds)
                .eq(AdRole::getIsDeleted, CommonConstant.NO_STR);

        return list(roleQueryWrapper);
    }

    
    public List<AdRole> findAllEnabled() {
        LambdaQueryWrapper<AdRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdRole::getIsDeleted, CommonConstant.NO_STR)
                .orderByDesc(AdRole::getCreateTime);
        return list(queryWrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteRole(String id) {
        AdRole role = getById(id);
        if (role == null) {
            throw new RuntimeException("角色不存在");
        }

        // 检查角色是否被用户关联
        LambdaQueryWrapper<AdUserRole> userRoleQueryWrapper = new LambdaQueryWrapper<>();
        userRoleQueryWrapper.eq(AdUserRole::getRoleId, id)
                .eq(AdUserRole::getIsDeleted, CommonConstant.NO_STR);

        if (adUserRoleMapper.selectCount(userRoleQueryWrapper) > 0) {
            throw new RuntimeException("角色已被用户关联，无法删除");
        }

        // 检查角色是否被权限关联
        LambdaQueryWrapper<AdRolePermission> rolePermissionQueryWrapper = new LambdaQueryWrapper<>();
        rolePermissionQueryWrapper.eq(AdRolePermission::getRoleId, id)
                .eq(AdRolePermission::getIsDeleted, CommonConstant.NO_STR);

        if (adRolePermissionMapper.selectCount(rolePermissionQueryWrapper) > 0) {
            throw new RuntimeException("角色已被权限关联，无法删除");
        }

        // 软删除角色
        role.setIsDeleted(CommonConstant.YES_STR);
        role.setUpdateTime(new Date());
        return updateById(role);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteRoles(List<String> ids) {
        return removeByIds(ids);
    }
}
