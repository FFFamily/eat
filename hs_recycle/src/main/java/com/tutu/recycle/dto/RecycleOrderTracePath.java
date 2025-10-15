package com.tutu.recycle.dto;

import com.tutu.recycle.entity.order.RecycleOrder;

import com.tutu.recycle.schema.RecycleOrderInfo;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecycleOrderTracePath {
    // 对应订单ID
    private String orderId;
    // 对应订单
    private Order context;

    @Data
    public static class Order{
        // 订单编号
        private String no;
        // 订单类型
        private String type;
        // 订单识别码
        private String identifyCode;

        public static Order convert(RecycleOrder order){
            Order o = new Order();
            o.setNo(order.getNo());
            o.setType(order.getType());
            o.setIdentifyCode(order.getIdentifyCode());
            return o;
        }
    }

}
