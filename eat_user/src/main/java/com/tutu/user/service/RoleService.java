package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Role;
import com.tutu.user.entity.User;
import com.tutu.user.entity.UserRole;
import com.tutu.user.enums.BaseUserRoleEnum;
import com.tutu.user.mapper.RoleMapper;
import com.tutu.user.mapper.UserMapper;
import com.tutu.user.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {
    @Resource
    private UserRoleMapper userRoleMapper;
    /**
     * 第一次创建用户绑定角色
     * @param userId 用户id
     */
    public void firstCreateUserBindRole(String userId) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        Role role = getOne(new LambdaQueryWrapper<Role>().eq(Role::getCode, BaseUserRoleEnum.USER.getCode()));
        userRole.setRoleId(role.getId());
        userRoleMapper.insert(userRole);
    }
}
