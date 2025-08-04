package com.tutu.lease.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.lease.entity.LeaseOrderItem;
import com.tutu.lease.mapper.LeaseOrderItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 租赁订单明细服务实现类
 */
@Service
public class LeaseOrderItemService extends ServiceImpl<LeaseOrderItemMapper, LeaseOrderItem> {

    
    public List<LeaseOrderItem> getByOrderId(String orderId) {
        LambdaQueryWrapper<LeaseOrderItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseOrderItem::getOrderId, orderId)
                .eq(LeaseOrderItem::getIsDeleted, CommonConstant.NO_STR)
                .orderByAsc(LeaseOrderItem::getCreateTime);
        return list(queryWrapper);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSave(List<LeaseOrderItem> orderItems) {
        return saveBatch(orderItems);
    }
}
