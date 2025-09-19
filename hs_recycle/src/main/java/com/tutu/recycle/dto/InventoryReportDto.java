package com.tutu.recycle.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 库存统计报表DTO
 */
@Getter
@Setter
public class InventoryReportDto {
    
    /**
     * 货物编号
     */
    private String goodNo;
    
    /**
     * 货物名称
     */
    private String goodName;
    
    /**
     * 货物分类
     */
    private String goodType;
    
    /**
     * 货物型号
     */
    private String goodModel;
    
    /**
     * 总数量
     */
    private Integer totalQuantity;
    
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
}