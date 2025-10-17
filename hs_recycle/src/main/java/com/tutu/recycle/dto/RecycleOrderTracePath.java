package com.tutu.recycle.dto;

import java.util.Objects;

import com.tutu.recycle.entity.order.RecycleOrder;

import com.tutu.recycle.schema.RecycleOrderInfo;

import cn.hutool.core.util.StrUtil;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RecycleOrderTracePath {
    // 对应订单ID
    private String orderId;
    // 改变原因
    private String changeReason;
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
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        RecycleOrderTracePath other = (RecycleOrderTracePath) obj;
        return StrUtil.equals(this.orderId, other.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

}
