package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户年度统计汇总DTO
 */
@Getter
@Setter
public class UserYearlyStatsDto {
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 统计年份
     */
    private Integer year;
    
    /**
     * 结算收益 - 收回订单中类型为采购订单的状态为已结算订单的订单总金额之和
     */
    private BigDecimal settlementIncome;
    
    /**
     * 运输收益 - 运输订单相关收益（待后续添加）
     */
    private BigDecimal transportIncome;
    
    /**
     * 加工收益 - 加工订单相关收益（待后续添加）
     */
    private BigDecimal processingIncome;
    
    /**
     * 仓储收益 - 仓储订单相关收益（待后续添加）
     */
    private BigDecimal storageIncome;
    
    /**
     * 销售收益 - 销售订单相关收益（待后续添加）
     */
    private BigDecimal salesIncome;
    
    /**
     * 其他收益 - 其他类型订单相关收益（待后续添加）
     */
    private BigDecimal otherIncome;
    
    /**
     * 总收益
     */
    private BigDecimal totalIncome;
    
    /**
     * 采购订单数量
     */
    private Integer purchaseOrderCount;
    
    /**
     * 运输订单数量
     */
    private Integer transportOrderCount;
    
    /**
     * 加工订单数量
     */
    private Integer processingOrderCount;
    
    /**
     * 仓储订单数量
     */
    private Integer storageOrderCount;
    
    /**
     * 销售订单数量
     */
    private Integer salesOrderCount;
    
    /**
     * 其他订单数量
     */
    private Integer otherOrderCount;
    
    /**
     * 总订单数量
     */
    private Integer totalOrderCount;
    
    /**
     * 采购合同数量
     */
    private Integer purchaseContractCount;
    
    /**
     * 采购订单数量（所有状态的采购订单）
     */
    private Integer allPurchaseOrderCount;
    
    /**
     * 采购订单已结算订单的合计重量
     */
    private BigDecimal settledPurchaseOrderWeight;
    
    /**
     * 统计生成时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date generateTime;
}