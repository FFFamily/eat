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
 * 入库单实体
 */
@Getter
@Setter
@TableName("inventory_in")
public class InventoryIn extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 入库单号
     */
    @TableField("in_no")
    private String inNo;
    
    /**
     * 仓库ID
     */
    @TableField("warehouse_id")
    private String warehouseId;
    
    /**
     * 入库类型
     */
    @TableField("in_type")
    private String inType;
    
    /**
     * 来源订单ID
     */
    @TableField("source_order_id")
    private String sourceOrderId;
    
    /**
     * 来源订单号
     */
    @TableField("source_order_no")
    private String sourceOrderNo;
    
    /**
     * 总数量
     */
    @TableField("total_quantity")
    private BigDecimal totalQuantity;
    
    /**
     * 入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("in_time")
    private Date inTime;
    
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
    
    /**
     * 单据状态
     */
    @TableField("status")
    private String status;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 仓库名称（非数据库字段）
     */
    @TableField(exist = false)
    private String warehouseName;
}

