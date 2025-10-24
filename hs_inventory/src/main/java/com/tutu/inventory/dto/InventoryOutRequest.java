package com.tutu.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 出库请求
 */
@Getter
@Setter
public class InventoryOutRequest {
    
    /**
     * 出库单ID（更新时需要）
     */
    private String id;
    
    /**
     * 仓库ID
     */
    private String warehouseId;
    
    /**
     * 出库类型
     */
    private String outType;
    
    /**
     * 目标订单ID
     */
    private String targetOrderId;
    
    /**
     * 目标订单号
     */
    private String targetOrderNo;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 出库明细列表
     */
    private List<InventoryOutItemRequest> items;
    
    /**
     * 出库明细
     */
    @Getter
    @Setter
    public static class InventoryOutItemRequest {
        /**
         * 货物ID
         */
        private String goodId;
        
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
         * 出库数量
         */
        private String outQuantity;
        
        /**
         * 单位
         */
        private String unit;
        
        /**
         * 备注
         */
        private String remark;
    }
}

