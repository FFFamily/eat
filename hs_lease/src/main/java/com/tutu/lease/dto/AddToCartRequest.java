package com.tutu.lease.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 添加到购物车请求DTO
 */
@Getter
@Setter
public class AddToCartRequest {
    
    /**
     * 商品ID
     */
    @NotBlank(message = "商品ID不能为空")
    private String goodId;
    
    /**
     * 租赁数量
     */
    @NotNull(message = "租赁数量不能为空")
    @Min(value = 1, message = "租赁数量不能小于1")
    private Integer quantity;
    
    /**
     * 租赁开始时间
     */
    @NotNull(message = "租赁开始时间不能为空")
    private Date leaseStartTime;
    
    /**
     * 租赁结束时间
     */
    @NotNull(message = "租赁结束时间不能为空")
    private Date leaseEndTime;
    
    /**
     * 备注
     */
    private String remark;
}
