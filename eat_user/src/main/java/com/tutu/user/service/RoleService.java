package com.tutu.user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.user.entity.Role;
import com.tutu.user.entity.User;
import com.tutu.user.mapper.RoleMapper;
import com.tutu.user.mapper.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class RoleService extends ServiceImpl<RoleMapper, Role> {
}
