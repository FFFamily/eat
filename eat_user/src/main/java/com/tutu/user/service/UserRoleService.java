package com.tutu.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Role;
import com.tutu.user.entity.UserRole;
import com.tutu.user.mapper.RoleMapper;
import com.tutu.user.mapper.UserRoleMapper;
import org.springframework.stereotype.Service;

@Service
public class UserRoleService extends ServiceImpl<UserRoleMapper, UserRole> {
}
