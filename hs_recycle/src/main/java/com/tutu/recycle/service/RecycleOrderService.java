package com.tutu.recycle.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.mapper.RecycleOrderMapper;
import org.springframework.stereotype.Service;

@Service
public class RecycleOrderService extends ServiceImpl<RecycleOrderMapper, RecycleOrder> {
}
