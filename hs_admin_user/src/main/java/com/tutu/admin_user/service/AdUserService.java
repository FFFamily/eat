package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdUserDTO;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.AdUserRole;
import com.tutu.admin_user.mapper.AdUserMapper;
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
 * 管理员用户服务实现类
 */
@Service
public class AdUserService extends ServiceImpl<AdUserMapper, AdUser> {

    @Autowired
    private AdUserRoleMapper adUserRoleMapper;

    
    public AdUser findByUsername(String username) {
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getUsername, username)
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);
        return getOne(queryWrapper);
    }

    
    public IPage<AdUser> getPageList(int current, int size, String keyword) {
        Page<AdUser> page = new Page<>(current, size);
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(AdUser::getIsDeleted, CommonConstant.NO_STR);

        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AdUser::getUsername, keyword)
                    .or()
                    .like(AdUser::getName, keyword)
                    .or()
                    .like(AdUser::getEmail, keyword)
            );
        }

        queryWrapper.orderByDesc(AdUser::getCreateTime);

        return page(page, queryWrapper);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(AdUser user) {
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 设置默认密码并加密
        if (StrUtil.isBlank(user.getPassword())) {
            user.setPassword(MD5.create().digestHex("123456"));
        } else {
            user.setPassword(MD5.create().digestHex(user.getPassword()));
        }

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }

        return save(user);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(AdUser user) {
        AdUser existUser = getById(user.getId());
        if (existUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 检查用户名是否被其他用户使用
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getUsername, user.getUsername())
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR)
                .ne(AdUser::getId, user.getId());

        if (getOne(queryWrapper) != null) {
            throw new RuntimeException("用户名已被使用");
        }

        // 如果有新密码，则加密
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(MD5.create().digestHex(user.getPassword()));
        } else {
            user.setPassword(null); // 不更新密码
        }

        return updateById(user);
    }

    
    public boolean resetPassword(String id, String newPassword) {
        AdUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, id)
                .set(AdUser::getPassword, MD5.create().digestHex(newPassword))
                .set(AdUser::getUpdateTime, new Date());

        return update(updateWrapper);
    }

    
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        AdUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!user.getPassword().equals(MD5.create().digestHex(oldPassword))) {
            throw new RuntimeException("原密码错误");
        }

        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, id)
                .set(AdUser::getPassword, MD5.create().digestHex(newPassword))
                .set(AdUser::getUpdateTime, new Date());

        return update(updateWrapper);
    }

    
    public boolean changeStatus(String id, Integer status) {
        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, id)
                .set(AdUser::getStatus, status)
                .set(AdUser::getUpdateTime, new Date());

        return update(updateWrapper);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(String userId, List<String> roleIds) {
        // 先删除原有角色关联
        LambdaUpdateWrapper<AdUserRole> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(AdUserRole::getUserId, userId)
                .set(AdUserRole::getIsDeleted, CommonConstant.YES_STR)
                .set(AdUserRole::getUpdateTime, new Date());
        adUserRoleMapper.update(null, deleteWrapper);

        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (String roleId : roleIds) {
                AdUserRole userRole = new AdUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreateTime(new Date());
                userRole.setUpdateTime(new Date());
                userRole.setIsDeleted(CommonConstant.NO_STR);
                adUserRoleMapper.insert(userRole);
            }
        }

        return true;
    }

    
    public List<AdUser> findByDeptId(String deptId) {
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getDeptId, deptId)
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);
        return list(queryWrapper);
    }

    
    public List<AdUser> findByRoleId(String roleId) {
        // 先查询用户角色关联表
        LambdaQueryWrapper<AdUserRole> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(AdUserRole::getRoleId, roleId)
                .eq(AdUserRole::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserRole> userRoles = adUserRoleMapper.selectList(roleQueryWrapper);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 提取用户ID列表
        List<String> userIds = userRoles.stream()
                .map(AdUserRole::getUserId)
                .toList();

        // 查询用户信息
        LambdaQueryWrapper<AdUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.in(AdUser::getId, userIds)
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);

        return list(userQueryWrapper);
    }

    public boolean deleteUser(String id) {
        return removeById(id);
    }

    public boolean batchDeleteUsers(List<String> ids) {
        return removeByIds(ids);
    }
}
