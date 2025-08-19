package com.tutu.recycle.service;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleOrderItem;
import com.tutu.recycle.mapper.RecycleOrderItemMapper;

/**
 * 回收订单项服务
 */
@Service
public class RecycleOrderItemService extends ServiceImpl<RecycleOrderItemMapper, RecycleOrderItem> {

}
