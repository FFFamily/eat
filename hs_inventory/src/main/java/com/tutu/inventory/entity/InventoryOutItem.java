package com.tutu.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 出库明细实体
 */
@Getter
@Setter
@TableName("inventory_out_item")
public class InventoryOutItem extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 出库单ID
     */
    @TableField("out_id")
    private String outId;
    
    /**
     * 货物ID
     */
    @TableField("good_id")
    private String goodId;
    
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
     * 出库数量
     */
    @TableField("out_quantity")
    private BigDecimal outQuantity;
    
    /**
     * 单位
     */
    @TableField("unit")
    private String unit;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

