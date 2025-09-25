package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.math.BigDecimal;

import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecycleOrderItem extends BaseEntity {
    // 合同ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 订单id
    private String recycleOrderId;
    // 货物编号
    private String goodNo;
    // 货物分类
    private String goodType;
    // 货物名称
    private String goodName;
    // 货物型号
    private String goodModel;
    // 货物数量
    private Integer goodCount;
    // 货物重量
    private BigDecimal goodWeight;
    // 合同预计单价
    private BigDecimal contractPrice;
    // 货物单价
    private BigDecimal goodPrice;
    // 货物总价
    private BigDecimal goodTotalPrice;
    // 评级系数
    private BigDecimal ratingCoefficient;
    // 评级调价金额
    private BigDecimal ratingAdjustAmount;
    // 其他调价
    private BigDecimal otherAdjustAmount;
    // 货物备注
    private String goodRemark;
}
