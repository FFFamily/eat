package com.tutu.recycle.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存统计报表DTO
 */
@Getter
@Setter
public class InventoryReportDto {
    
    /**
     * 货物编号
     */
    private String goodNo;
    
    /**
     * 货物名称
     */
    private String goodName;
    
    /**
     * 货物分类
     */
    private String goodType;
    
    /**
     * 货物型号
     */
    private String goodModel;
    
    /**
     * 入库总数量
     */
    private Integer totalInQuantity;
    
    /**
     * 出库总数量
     */
    private Integer totalOutQuantity;
    
    /**
     * 当前库存数量
     */
    private Integer currentStock;
    
    /**
     * 入库总金额
     */
    private BigDecimal totalInAmount;
    
    /**
     * 出库总金额
     */
    private BigDecimal totalOutAmount;
    
    /**
     * 库存总价值
     */
    private BigDecimal stockValue;
    
    /**
     * 平均单价
     */
    private BigDecimal averagePrice;
    
    /**
     * 最后入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastInTime;
    
    /**
     * 最后出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastOutTime;
    
    /**
     * 订单数量（包含该货物的订单总数）
     */
    private Integer orderCount;
    
    /**
     * 流转方向统计
     */
    private String flowDirectionStats;
}