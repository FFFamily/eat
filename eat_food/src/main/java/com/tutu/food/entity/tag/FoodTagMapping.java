package com.tutu.food.entity.tag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Getter;
import lombok.Setter;
// 食物和食物标签中间表
@Getter
@Setter
public class FoodTagMapping {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String foodId;
    private String tagId;
}
