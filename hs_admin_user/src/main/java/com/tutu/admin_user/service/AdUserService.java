package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.dto.AdUserDTO;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.AdUserRole;
import com.tutu.admin_user.mapper.AdUserMapper;
import com.tutu.admin_user.mapper.AdUserRoleMapper;
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
        return baseMapper.findByUsername(username);
    }
    
    
    public IPage<AdUser> getPageList(int current, int size, String keyword) {
        Page<AdUser> page = new Page<>(current, size);
        return baseMapper.selectPageWithDept(page, keyword);
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(AdUserDTO userDTO) {
        // 检查用户名是否已存在
        if (findByUsername(userDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        AdUser user = new AdUser();
        BeanUtils.copyProperties(userDTO, user);
        
        // 设置默认密码并加密
        if (StrUtil.isBlank(userDTO.getPassword())) {
            user.setPassword(MD5.create().digestHex("123456"));
        } else {
            user.setPassword(MD5.create().digestHex(userDTO.getPassword()));
        }
        
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        
        boolean result = save(user);
        
        // 分配角色
        if (result && userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
        
        return result;
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUser(AdUserDTO userDTO) {
        AdUser user = getById(userDTO.getId());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 检查用户名是否被其他用户使用
        AdUser existUser = findByUsername(userDTO.getUsername());
        if (existUser != null && !existUser.getId().equals(userDTO.getId())) {
            throw new RuntimeException("用户名已被使用");
        }
        
        BeanUtils.copyProperties(userDTO, user);
        
        // 如果有新密码，则加密
        if (StrUtil.isNotBlank(userDTO.getPassword())) {
            user.setPassword(MD5.create().digestHex(userDTO.getPassword()));
        }
        
        boolean result = updateById(user);
        
        // 重新分配角色
        if (result && userDTO.getRoleIds() != null) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }
        
        return result;
    }
    
    
    public boolean deleteUser(String id) {
        return removeById(id);
    }
    
    
    public boolean batchDeleteUsers(List<String> ids) {
        return removeByIds(ids);
    }
    
    
    public boolean resetPassword(String id, String newPassword) {
        AdUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setPassword(MD5.create().digestHex(newPassword));
        return updateById(user);
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
        
        user.setPassword(MD5.create().digestHex(newPassword));
        return updateById(user);
    }
    
    
    public boolean changeStatus(String id, Integer status) {
        AdUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        user.setStatus(status);
        return updateById(user);
    }
    
    
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(String userId, List<String> roleIds) {
        // 先删除原有角色关联
        adUserRoleMapper.deleteByUserId(userId);
        
        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            List<AdUserRole> userRoles = new ArrayList<>();
            for (String roleId : roleIds) {
                AdUserRole userRole = new AdUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRole.setCreateTime(new Date());
                userRole.setUpdateTime(new Date());
                userRoles.add(userRole);
            }
            adUserRoleMapper.batchInsert(userRoles);
        }
        
        return true;
    }
    
    
    public List<AdUser> findByDeptId(String deptId) {
        return baseMapper.findByDeptId(deptId);
    }
    
    
    public List<AdUser> findByRoleId(String roleId) {
        return baseMapper.findByRoleId(roleId);
    }
}
