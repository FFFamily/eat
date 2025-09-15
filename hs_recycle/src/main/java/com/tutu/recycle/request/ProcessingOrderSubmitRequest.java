package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 加工订单提交请求
 */
@Getter
@Setter
public class ProcessingOrderSubmitRequest {
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单图片
     */
    private String orderNodeImg;
    
    /**
     * 订单明细列表
     */
    private List<OrderItemUpdateRequest> items;
    
    /**
     * 订单明细添加请求
     */
    @Getter
    @Setter
    public static class OrderItemUpdateRequest {
        /**
         * 货物编号
         */
        private String goodNo;
        
        /**
         * 货物类型
         */
        private String goodType;
        
        /**
         * 货物名称
         */
        private String goodName;
        
        /**
         * 货物规格
         */
        private String goodModel;
        
        /**
         * 货物数量
         */
        private Integer goodCount;
        
        /**
         * 货物重量
         */
        private java.math.BigDecimal goodWeight;
        
        /**
         * 货物单价
         */
        private java.math.BigDecimal goodPrice;
        
        /**
         * 货物总价
         */
        private java.math.BigDecimal goodTotalPrice;
        
        /**
         * 货物备注
         */
        private String goodRemark;
    }
}