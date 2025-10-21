package com.tutu.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 库存统计VO
 */
@Getter
@Setter
public class InventoryStatisticsVO {
    
    /**
     * 仓库ID
     */
    private String warehouseId;
    
    /**
     * 仓库名称
     */
    private String warehouseName;
    
    /**
     * 货物总数量
     */
    private Integer totalGoodsCount;
    
    /**
     * 预警货物数量
     */
    private Integer warningGoodsCount;
    
    /**
     * 库存总量
     */
    private BigDecimal totalStock;
    
    /**
     * 可用库存总量
     */
    private BigDecimal totalAvailableStock;
    
    /**
     * 锁定库存总量
     */
    private BigDecimal totalLockedStock;
}

