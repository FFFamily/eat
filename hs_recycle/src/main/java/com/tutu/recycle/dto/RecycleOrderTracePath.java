package com.tutu.recycle.dto;

import com.tutu.recycle.entity.order.RecycleOrder;

import lombok.Data;

@Data
public class RecycleOrderTracePath {
    private String prev;
    private String next;
    private RecycleOrder current;
}
