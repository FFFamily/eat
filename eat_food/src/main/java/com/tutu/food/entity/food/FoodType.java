package com.tutu.food.entity.food;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FoodType extends BaseEntity {
    // ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 类型名称
    private String name;
    // 状态
    private Integer status;
}
