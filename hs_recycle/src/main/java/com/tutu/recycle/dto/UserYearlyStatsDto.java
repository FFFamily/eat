package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
     * 已开票的金额
     */
    private BigDecimal settledInvoiceAmount;
    /**
     * 未开票的金额
     */
    private BigDecimal notSettledInvoiceAmount;
    /**
     * 结算收益
     */
    private BigDecimal orderAmount;
    /*
     * 采购订单数量
     */
    private Integer purchaseOrderCount;
    /**
     * 回收合同数量
     */
    private Integer contractCount;
    // 货物重量（回收重量）
    private BigDecimal totalWeight;
    // 评级调价（评级收益）
    private BigDecimal ratingAdjustAmount;
    // 货款金额（主业务收益）
    private BigDecimal orderFundAmount;
    // 走款金额（实际入账）
    private BigDecimal fundFlowAmount;
    // 应走款金额（待入账）
    private BigDecimal expectedFundFlowAmount;
    private List<MonthlyStatsDto> monthlyStats;
}