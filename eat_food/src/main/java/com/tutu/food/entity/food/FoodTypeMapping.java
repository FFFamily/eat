package com.tutu.food.entity.food;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * 食物类型中间表
 */
@Data
public class FoodTypeMapping {
    // 编号
    private String id;

    @TableField(fill = FieldFill.INSERT)
    private String tenantId;
    // 食物id
    private String foodId;
    // 食物类型id
    private String foodTypeId;
}
