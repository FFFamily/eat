package com.tutu.recycle.request;

import java.util.List;

import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.entity.RecycleOrderItem;

import lombok.Getter;
import lombok.Setter;

/**
 * 创建回收订单请求
 */
@Getter
@Setter
public class CreateRecycleOrderRequest extends RecycleOrder{
    private List<RecycleOrderItem> items;
}
