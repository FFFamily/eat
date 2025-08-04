package com.tutu.lease.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.lease.entity.LeaseGood;
import com.tutu.lease.entity.LeaseGoodCategory;
import com.tutu.lease.mapper.LeaseGoodCategoryMapper;
import com.tutu.lease.mapper.LeaseGoodMapper;
import org.springframework.stereotype.Service;

@Service
public class LeaseGoodService extends ServiceImpl<LeaseGoodMapper, LeaseGood> {
}