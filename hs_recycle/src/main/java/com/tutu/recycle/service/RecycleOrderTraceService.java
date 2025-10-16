package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.order.RecycleOrderTrace;
import com.tutu.recycle.mapper.RecycleOrderTraceMapper;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * 回收订单轨迹服务类
 */
@Service
public class RecycleOrderTraceService extends ServiceImpl<RecycleOrderTraceMapper, RecycleOrderTrace> {
    /**
     * 根据订单ID获取轨迹列表
     * @param orderId 订单ID
     * @return 轨迹列表
     */
    public List<RecycleOrderTrace> getByOrderId(String orderId) {
        return baseMapper.selectList(new LambdaQueryWrapper<RecycleOrderTrace>().eq(RecycleOrderTrace::getOrderId, orderId));
    }
    /**
     * 根据订单ID获取子订单轨迹列表
     * @param orderId 订单ID
     * @return 子订单轨迹列表
     */
    public List<RecycleOrderTrace> getChildrenByOrderId(String orderId) {
        return baseMapper.selectList(new LambdaQueryWrapper<RecycleOrderTrace>().eq(RecycleOrderTrace::getParentOrderId, orderId));
    }
    /**
     * 根据订单ID删除轨迹
     * @param orderId 订单ID
     */
    public void removeByOrderId(String orderId) {
        baseMapper.delete(new LambdaQueryWrapper<RecycleOrderTrace>().eq(RecycleOrderTrace::getOrderId, orderId));
    }
}
