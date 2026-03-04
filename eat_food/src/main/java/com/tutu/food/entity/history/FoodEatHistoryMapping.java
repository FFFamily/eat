package com.tutu.food.entity.history;

import com.tutu.common.entity.TenantBaseEntity;

// 食物-饮食关中间表
public class FoodEatHistoryMapping extends TenantBaseEntity {
    private String id;
    private String foodId;
    private String eatHistoryId;
}
