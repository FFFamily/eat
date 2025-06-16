package com.tutu.food.entity.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;
// 食物和食物标签中间表
@Getter
@Setter
public class FoodTagMapping extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String foodId;
    private String tagId;
}
