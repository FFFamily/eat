package com.tutu.food.entity.history;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

// 饮食历史
@Getter
@Setter
public class EatHistory extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 食物ID
    private String foodId;
    // 食物名称
    @TableField(exist = false)
    private String foodName;
    // 餐次id
    private String mealTimeId;
    // 餐次名称
    @TableField(exist = false)
    private String mealTimeName;
}
