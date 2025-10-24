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
 * 出库单实体
 */
@Getter
@Setter
@TableName("inventory_out")
public class InventoryOut extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 出库单号
     */
    @TableField("out_no")
    private String outNo;
    
    /**
     * 仓库ID
     */
    @TableField("warehouse_id")
    private String warehouseId;
    
    /**
     * 出库类型
     */
    @TableField("out_type")
    private String outType;
    
    /**
     * 目标订单ID
     */
    @TableField("target_order_id")
    private String targetOrderId;
    
    /**
     * 目标订单号
     */
    @TableField("target_order_no")
    private String targetOrderNo;
    
    /**
     * 总数量
     */
    @TableField("total_quantity")
    private BigDecimal totalQuantity;
    
    /**
     * 出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("out_time")
    private Date outTime;
    
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

