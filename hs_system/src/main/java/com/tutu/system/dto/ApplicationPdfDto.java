package com.tutu.system.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 业务申请单PDF数据DTO
 */
@Data
public class ApplicationPdfDto {
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 订单编号
     */
    private String orderNo;
    
    /**
     * 订单类型（purchase-采购, sales-销售, transport-运输, process-加工, storage-仓储）
     */
    private String orderType;
    
    /**
     * 甲方
     */
    private String partyA;
    
    /**
     * 乙方
     */
    private String partyB;
    
    /**
     * 合同编号
     */
    private String contractNo;
    
    /**
     * 合同名称
     */
    private String contractName;
    
    /**
     * 订单明细列表
     */
    private List<OrderItemDto> orderItems;
    
    /**
     * 订单交付地址
     */
    private String deliveryAddress;
    
    /**
     * 仓库地址（交付货物送达我司地址）
     */
    private String warehouseAddress;
    
    /**
     * 经办人
     */
    private String processor;
    
    /**
     * 经办人电话
     */
    private String processorPhone;
    
    /**
     * 订单申请时间
     */
    private Date startTime;
    
    /**
     * 合同约定走款账号
     */
    private String paymentAccount;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 订单明细DTO
     */
    @Data
    public static class OrderItemDto {
        /**
         * 分类（goods-货物, transport-运输, service-服务）
         */
        private String type;
        
        /**
         * 名称
         */
        private String name;
        
        /**
         * 规格型号
         */
        private String specification;
        
        /**
         * 备注
         */
        private String remark;
        
        /**
         * 数量
         */
        private BigDecimal quantity;
        
        /**
         * 单价
         */
        private BigDecimal unitPrice;
        
        /**
         * 总价
         */
        private BigDecimal amount;
    }
}

