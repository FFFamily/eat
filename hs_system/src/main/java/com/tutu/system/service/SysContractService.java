package com.tutu.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.system.entity.SysContract;
import com.tutu.system.mapper.SysContractMapper;
import org.springframework.stereotype.Service;

@Service
public class SysContractService extends ServiceImpl<SysContractMapper, SysContract> {
}
