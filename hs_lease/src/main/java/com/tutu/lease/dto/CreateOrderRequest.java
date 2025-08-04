package com.tutu.lease.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 创建订单请求DTO
 */
@Getter
@Setter
public class CreateOrderRequest {
    
    /**
     * 购物车ID列表
     */
    @NotEmpty(message = "购物车ID列表不能为空")
    private List<String> cartIds;
    
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
}
