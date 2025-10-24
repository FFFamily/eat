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
 * 库存实体
 */
@Getter
@Setter
@TableName("inventory")
public class Inventory extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
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
     * 货物类型
     */
    @TableField("good_type")
    private String goodType;
    
    /**
     * 规格型号
     */
    @TableField("good_model")
    private String goodModel;
    
    /**
     * 当前库存数量
     */
    @TableField("current_stock")
    private BigDecimal currentStock;
    
    /**
     * 可用库存数量
     */
    @TableField("available_stock")
    private BigDecimal availableStock;
    
    /**
     * 锁定库存数量
     */
    @TableField("locked_stock")
    private BigDecimal lockedStock;
    
    /**
     * 最小库存（安全库存）
     */
    @TableField("min_stock")
    private BigDecimal minStock;
    
    /**
     * 最大库存
     */
    @TableField("max_stock")
    private BigDecimal maxStock;
    
    /**
     * 单位
     */
    @TableField("unit")
    private String unit;
    
    /**
     * 最后入库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("last_in_time")
    private Date lastInTime;
    
    /**
     * 最后出库时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField("last_out_time")
    private Date lastOutTime;
    
    /**
     * 仓库名称（非数据库字段）
     */
    @TableField(exist = false)
    private String warehouseName;
}

