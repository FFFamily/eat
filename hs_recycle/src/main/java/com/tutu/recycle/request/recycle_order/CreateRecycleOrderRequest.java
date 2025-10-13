package com.tutu.recycle.request.recycle_order;

import java.util.List;

import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.order.RecycleOrderItem;

import lombok.Getter;
import lombok.Setter;

/**
 * 创建回收订单请求
 */
@Getter
@Setter
public class CreateRecycleOrderRequest extends RecycleOrder{
    private List<RecycleOrderItem> items;
    // 轨迹能力数据
    private List<SourceCode> sourceCodes;
}
