package com.tutu.admin_user.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.SysLoginLog;
import com.tutu.admin_user.mapper.SysLoginLogMapper;
import org.springframework.stereotype.Service;

@Service
public class SysLoginLogService extends ServiceImpl<SysLoginLogMapper, SysLoginLog> {
}

