package com.tutu.recycle.server;

import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.schema.RecycleOrderInfo;

/**
 * 回收订单Server接口
 * 各个订单类型的Server需要实现此接口来处理特定阶段的属性赋值
 */
public interface RecycleOrderServer {
    
    /**
     * 填充订单的特定属性
     * @param recycleOrder 回收订单对象
     * @param userOrderDTO 用户订单DTO
     */
    void fillOrderProperties(RecycleOrderInfo recycleOrder, UserOrderDTO userOrderDTO);
}

