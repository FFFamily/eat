package com.tutu.food.entity.food;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Food extends BaseEntity {
    // ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 食物名称
    private String name;
    // 食物类型ID: （如：水果、主食、蔬菜等）的ID
    private Integer foodTypeId;
    // 状态
    private Integer status;
    // 描述
    private String description;
    // 次数
//    private Long eatCount;
}
