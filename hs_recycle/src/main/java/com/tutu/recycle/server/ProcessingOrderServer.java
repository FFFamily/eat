package com.tutu.recycle.server;

import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.schema.RecycleOrderInfo;
import org.springframework.stereotype.Component;

/**
 * 加工订单Server
 * 处理加工订单的特定属性赋值
 */
@Component
public class ProcessingOrderServer implements RecycleOrderServer {
    
    @Override
    public void fillOrderProperties(RecycleOrderInfo recycleOrder, UserOrderDTO userOrderDTO) {
        // 处理货物明细
        if (userOrderDTO.getItems() != null) {
            recycleOrder.setItems(userOrderDTO.getItems());
        }
    }
}

