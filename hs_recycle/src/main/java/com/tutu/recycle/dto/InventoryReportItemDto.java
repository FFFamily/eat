package com.tutu.recycle.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 库存明细DTO
 */
@Getter
@Setter
public class InventoryReportItemDto {
    // 货物编号
    private String goodNo;
    // 货物名称
    private String goodName;
    // 货物类型
    private String goodType;
    // 货物型号
    private String goodModel;
    // 货物数量
    private Integer goodCount;
    // 货物总价
    private BigDecimal goodTotalPrice;
    // 回收订单编号
    private String recycleOrderNo;
    // 回收订单流向
    private String recycleOrderFlowDirection;
    // 回收订单类型
    private String recycleOrderType;
}