package com.tutu.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 库存预警VO
 */
@Getter
@Setter
public class InventoryStockWarningVO {
    
    /**
     * 仓库ID
     */
    private String warehouseId;
    
    /**
     * 仓库名称
     */
    private String warehouseName;
    
    /**
     * 货物编号
     */
    private String goodNo;
    
    /**
     * 货物名称
     */
    private String goodName;
    
    /**
     * 货物类型
     */
    private String goodType;
    
    /**
     * 规格型号
     */
    private String goodModel;
    
    /**
     * 当前库存
     */
    private BigDecimal currentStock;
    
    /**
     * 可用库存
     */
    private BigDecimal availableStock;
    
    /**
     * 最小库存
     */
    private BigDecimal minStock;
    
    /**
     * 单位
     */
    private String unit;
    
    /**
     * 预警类型
     */
    private String warningType;
}

