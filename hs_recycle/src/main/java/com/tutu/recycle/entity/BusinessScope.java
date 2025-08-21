package com.tutu.recycle.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 经营范围
 */
@Getter
@Setter
public class BusinessScope extends BaseEntity{
    // 经营范围id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 编号
    private String no;
    // 排序
    private Integer sortNum;
    // 货物类型
    private String goodType;
    // 货物名称
    private String goodName;
    // 规格型号
    private String goodModel;
    // 货物备注
    private String goodRemark;
    // 是否显示
    private String isShow;
    // 公示价格
    private BigDecimal publicPrice;
}
