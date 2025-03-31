package com.tutu.food.entity.food;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

// 食物饮食方式
@Getter
@Setter
public class FoodDietStyle extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 名称
    private String foodId;
    // 编码
    private String dietStyleId;
}
