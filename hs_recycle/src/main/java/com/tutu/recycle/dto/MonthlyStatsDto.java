package com.tutu.recycle.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 月度统计DTO
 */
@Getter
@Setter
public class MonthlyStatsDto {
    /**
     * 月份 (1-12)
     */
    private Integer month;
    
    /**
     * 当月重量
     */
    private BigDecimal weight;
    
    /**
     * 当月结算收益
     */
    private BigDecimal settlementAmount;
    
    /**
     * 当月订单数量
     */
    private Integer orderCount;
    
    public MonthlyStatsDto() {
        this.weight = BigDecimal.ZERO;
        this.settlementAmount = BigDecimal.ZERO;
        this.orderCount = 0;
    }
    
    public MonthlyStatsDto(Integer month) {
        this();
        this.month = month;
    }
}
