package com.tutu.recycle.server;

import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.enums.RecycleFlowDirectionEnum;
import com.tutu.recycle.schema.RecycleOrderInfo;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * 仓储订单Server
 * 处理仓储订单的特定属性赋值
 */
@Component
public class StorageOrderServer implements RecycleOrderServer {
    
    @Override
    public void fillOrderProperties(RecycleOrderInfo recycleOrder, UserOrderDTO userOrderDTO) {
        Optional.ofNullable(userOrderDTO.getWarehouseId()).ifPresent(recycleOrder::setWarehouseId);
        
        // 设置流转方向为入库
        recycleOrder.setFlowDirection(RecycleFlowDirectionEnum.IN.getValue());
        // 设置货物明细
        Optional.ofNullable(userOrderDTO.getItems()).ifPresent(recycleOrder::setItems);
    }
}

