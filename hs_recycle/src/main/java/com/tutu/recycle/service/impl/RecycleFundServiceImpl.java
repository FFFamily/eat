package com.tutu.recycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.mapper.RecycleFundMapper;
import com.tutu.recycle.service.RecycleFundService;
import org.springframework.stereotype.Service;

@Service
public class RecycleFundServiceImpl extends ServiceImpl<RecycleFundMapper, RecycleFund> implements RecycleFundService {
} 