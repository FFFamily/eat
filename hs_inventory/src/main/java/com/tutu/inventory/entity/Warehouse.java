package com.tutu.inventory.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 仓库实体
 */
@Getter
@Setter
@TableName("warehouse")
public class Warehouse extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 仓库编号
     */
    @TableField("warehouse_no")
    private String warehouseNo;
    
    /**
     * 仓库名称
     */
    @TableField("warehouse_name")
    private String warehouseName;
    
    /**
     * 仓库地址
     */
    @TableField("warehouse_address")
    private String warehouseAddress;
    
    /**
     * 仓库类型
     */
    @TableField("warehouse_type")
    private String warehouseType;
    
    /**
     * 负责人ID
     */
    @TableField("manager_id")
    private String managerId;
    
    /**
     * 负责人姓名
     */
    @TableField("manager_name")
    private String managerName;
    
    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;
    
    /**
     * 状态
     */
    @TableField("status")
    private String status;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}

