package com.tutu.food.entity.habit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

// 食物习惯
@Getter
@Setter
@TableName(autoResultMap = true)
public class FoodHabit extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 习惯名称
    private String name;
    // 食物标签
    @TableField(typeHandler = JacksonTypeHandler.class)
    private FoodHabitTagInfo tag;
    // 描述
    private String description;
}
