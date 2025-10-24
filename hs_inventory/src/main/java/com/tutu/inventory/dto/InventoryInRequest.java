package com.tutu.inventory.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 入库请求
 */
@Getter
@Setter
public class InventoryInRequest {
    
    /**
     * 入库单ID（更新时需要）
     */
    private String id;
    
    /**
     * 仓库ID
     */
    private String warehouseId;
    
    /**
     * 入库类型
     */
    private String inType;
    
    /**
     * 来源订单ID
     */
    private String sourceOrderId;
    
    /**
     * 来源订单号
     */
    private String sourceOrderNo;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 入库明细列表
     */
    private List<InventoryInItemRequest> items;
    
    /**
     * 入库明细
     */
    @Getter
    @Setter
    public static class InventoryInItemRequest {
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
         * 入库数量
         */
        private String inQuantity;
        
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

