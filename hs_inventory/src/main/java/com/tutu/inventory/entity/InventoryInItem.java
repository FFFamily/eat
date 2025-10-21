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
 * 入库明细实体
 */
@Getter
@Setter
@TableName("inventory_in_item")
public class InventoryInItem extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 入库单ID
     */
    @TableField("in_id")
    private String inId;
    
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
     * 入库数量
     */
    @TableField("in_quantity")
    private BigDecimal inQuantity;
    
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

