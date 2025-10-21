package com.tutu.inventory.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 库存查询请求
 */
@Getter
@Setter
public class InventoryQueryRequest {
    
    /**
     * 仓库ID
     */
    private String warehouseId;
    
    /**
     * 货物编号
     */
    private String goodNo;
    
    /**
     * 货物名称（模糊查询）
     */
    private String goodName;
    
    /**
     * 货物类型
     */
    private String goodType;
    
    /**
     * 是否只查询预警库存
     */
    private Boolean warningOnly;
    
    /**
     * 当前页
     */
    private Integer page = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
}

