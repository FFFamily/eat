package com.tutu.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存流水实体
 */
@Getter
@Setter
@TableName("inventory_transaction")
public class InventoryTransaction extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 流水号
     */
    @TableField("transaction_no")
    private String transactionNo;
    
    /**
     * 仓库ID
     */
    @TableField("warehouse_id")
    private String warehouseId;
    
    /**
     * 货物编号
     */
    @TableField("good_no")
    private String goodNo;
    
    /**
     * 货物名称
     */
    @TableField("good_name")
    private String goodName;
    
    /**
     * 交易类型
     */
    @TableField("transaction_type")
    private String transactionType;
    
    /**
     * 业务类型
     */
    @TableField("business_type")
    private String businessType;
    
    /**
     * 数量（正数为入库，负数为出库）
     */
    @TableField("quantity")
    private BigDecimal quantity;
    
    /**
     * 变动前库存
     */
    @TableField("before_stock")
    private BigDecimal beforeStock;
    
    /**
     * 变动后库存
     */
    @TableField("after_stock")
    private BigDecimal afterStock;
    
    /**
     * 关联单号（入库单号或出库单号）
     */
    @TableField("related_no")
    private String relatedNo;
    
    /**
     * 交易时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("transaction_time")
    private Date transactionTime;
    
    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private String operatorId;
    
    /**
     * 操作人姓名
     */
    @TableField("operator_name")
    private String operatorName;
}

