package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecycleCapitalPool extends BaseEntity {
    // 资金池ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 编号
    private String no;

}
