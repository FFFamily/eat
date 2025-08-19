package com.tutu.recycle.schema;

import java.util.List;

import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.entity.RecycleOrderItem;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecycleOrderInfo extends RecycleOrder {
    private List<RecycleOrderItem> items;
    
}
