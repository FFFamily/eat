package com.tutu.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Role;
import com.tutu.user.entity.UserRole;
import com.tutu.user.enums.BaseUserRoleEnum;
import com.tutu.user.mapper.RoleMapper;
import com.tutu.user.mapper.UserRoleMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {
    @Resource
    private UserRoleMapper userRoleMapper;
}
