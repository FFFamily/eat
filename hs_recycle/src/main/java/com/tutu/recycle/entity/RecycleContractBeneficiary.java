package com.tutu.recycle.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 回收合同受益人实体
 */
@Getter
@Setter
public class RecycleContractBeneficiary extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 合同ID（关联回收合同表）
     */
    @TableField("contract_id")
    private String contractId;
    
    /**
     * 合同编号（非数据库字段，用于查询展示）
     */
    @TableField(exist = false)
    private String contractNo;
    
    /**
     * 合同名称（非数据库字段，用于查询展示）
     */
    @TableField(exist = false)
    private String contractName;
    
    /**
     * 受益人类型（主受益人/次受益人）
     * 建议枚举值：MAIN-主受益人，SECONDARY-次受益人
     */
    @TableField("beneficiary_type")
    private String beneficiaryType;
    
    /**
     * 受益人ID（可以是用户ID、合作方ID等，根据业务需求确定）
     */
    @TableField("beneficiary_id")
    private String beneficiaryId;
    
    /**
     * 受益人名称
     */
    @TableField(exist = false)
    private String beneficiaryName;
    
    /**
     * 分成比例（0-1之间的小数，如0.7表示70%）
     * 主受益人和次受益人的分成比例之和应该等于1
     */
    @TableField("share_ratio")
    private BigDecimal shareRatio;
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
}
