package com.tutu.recycle.server;

import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.schema.RecycleOrderInfo;
import org.springframework.stereotype.Component;

/**
 * 采购订单Server
 * 处理采购订单的特定属性赋值
 */
@Component
public class PurchaseOrderServer implements RecycleOrderServer {
    
    @Override
    public void fillOrderProperties(RecycleOrderInfo recycleOrder, UserOrderDTO userOrderDTO) {
        recycleOrder.setOrderNodeImg(userOrderDTO.getOrderNodeImg());
        // 设置交付地址（使用用户订单的位置信息）
        recycleOrder.setDeliveryAddress(userOrderDTO.getDeliveryAddress());
        // 走款账号
        recycleOrder.setPaymentAccount(userOrderDTO.getPaymentAccount());
        recycleOrder.setSiteName(userOrderDTO.getSiteName());
    }
}

