package com.tutu.recycle.enums;

import com.tutu.common.enums.BaseEnum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecycleOrderStatusEnum implements BaseEnum<RecycleOrderStatusEnum,String> {
    /**
     * 待回收
     */
    PENDING_RECYCLE("pending_recycle", "待回收"),
    /**
     * 待取件
     */
    PENDING_PICKUP("pending_pickup", "待取件"),
    /**
     * 已取件
     */
    PICKED_UP("picked_up", "已取件"),
    /**
     * 已完成
     */
    COMPLETED("completed", "已完成"),
    /**
     * 已取消
     */
    CANCELLED("cancelled", "已取消"),
    /**
     * 已退款
     */
    REFUNDED("refunded", "已退款");
    private final String code;
    private final String title;
}
