package com.tutu.recycle.server;

import cn.hutool.core.util.StrUtil;
import com.tutu.recycle.dto.UserOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.enums.TransportStatusEnum;
import com.tutu.recycle.schema.RecycleOrderInfo;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;

/**
 * 运输订单Server
 * 处理运输订单的特定属性赋值
 */
@Component
public class TransportOrderServer implements RecycleOrderServer {

    /**
     * 填充运输订单的特定属性
     * @param recycleOrder 运输订单对象
     * @param userOrderDTO 用户订单DTO
     */
    @Override
    public void fillOrderProperties(RecycleOrderInfo recycleOrder, UserOrderDTO userOrderDTO) {
        Optional.ofNullable(userOrderDTO.getPickupAddress()).ifPresent(recycleOrder::setPickupAddress);
        Optional.ofNullable(userOrderDTO.getDeliveryAddress()).ifPresent(recycleOrder::setDeliveryAddress);
        Optional.ofNullable(userOrderDTO.getStartTime()).ifPresent(recycleOrder::setStartTime);
        Optional.ofNullable(userOrderDTO.getEndTime()).ifPresent(recycleOrder::setEndTime);
        // 货物重量
        Optional.ofNullable(userOrderDTO.getGoodsWeight()).ifPresent(recycleOrder::setGoodsWeight);
        // 运输状态
        recycleOrder.setTransportStatus(TransportStatusEnum.ARRIVED.getCode());
        recycleOrder.setEndTime(new Date());
    }
}

