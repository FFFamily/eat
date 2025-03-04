package com.tutu.food.entity.history;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

// 饮食历史
public class EatHistory extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 食物ID列表
    private String foodId;
}
