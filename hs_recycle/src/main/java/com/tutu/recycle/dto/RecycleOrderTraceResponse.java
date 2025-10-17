package com.tutu.recycle.dto;

import java.util.List;
import java.util.Map;

import com.tutu.recycle.entity.order.RecycleOrder;
import lombok.Data;
@Data
public class RecycleOrderTraceResponse {
    private Map<String, List<RecycleOrderTracePath>> graph;
    private List<List<RecycleOrderTracePath>> paths;
    private List<RecycleOrder> allOrders;
}
