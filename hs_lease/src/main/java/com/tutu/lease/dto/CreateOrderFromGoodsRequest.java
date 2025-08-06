package com.tutu.lease.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 通过商品信息创建订单请求DTO
 */
@Getter
@Setter
public class CreateOrderFromGoodsRequest {
    
    /**
     * 商品信息列表
     */
    @NotEmpty(message = "商品信息列表不能为空")
    @Valid
    private List<OrderGoodsInfo> orderItems;
    
    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;
    
    /**
     * 收货人手机号
     */
    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;
    
    /**
     * 收货地址
     */
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;
    
    /**
     * 归还地址
     */
    private String returnAddress;
    
    /**
     * 订单备注
     */
    private String remark;

    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 订单商品信息
     */
    @Getter
    @Setter
    public static class OrderGoodsInfo {
        
        /**
         * 商品ID
         */
        @NotBlank(message = "商品ID不能为空")
        private String goodId;
        
        /**
         * 商品名称
         */
        @NotBlank(message = "商品名称不能为空")
        private String goodName;
        
        /**
         * 商品单价
         */
        @NotNull(message = "商品单价不能为空")
        @DecimalMin(value = "0", message = "商品单价必须大于0")
        private java.math.BigDecimal goodPrice;
        
        /**
         * 租赁数量
         */
        @NotNull(message = "租赁数量不能为空")
        @Min(value = 1, message = "租赁数量必须大于0")
        private Integer quantity;
        
        /**
         * 租赁开始时间
         */
        @NotNull(message = "租赁开始时间不能为空")
        private java.util.Date leaseStartTime;
        
        /**
         * 租赁结束时间
         */
        @NotNull(message = "租赁结束时间不能为空")
        private java.util.Date leaseEndTime;
        
        /**
         * 租赁天数
         */
        @NotNull(message = "租赁天数不能为空")
        @Min(value = 1, message = "租赁天数必须大于0")
        private Integer leaseDays;
        
        /**
         * 小计金额
         */
        @NotNull(message = "小计金额不能为空")
        @DecimalMin(value = "0", message = "小计金额必须大于0")
        private java.math.BigDecimal subtotal;
        
        /**
         * 备注
         */
        private String remark;
    }
} 