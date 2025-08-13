package com.tutu.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 合同更新日志实体
 */
@Getter
@Setter
public class SysContractLog extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 合同ID
     */
    private String contractId;
    /**
     * 操作类型：UPDATE-更新，CREATE-创建，DELETE-删除
     */
    private String operationType;
    /**
     * 操作类型名称
     */
    @TableField(exist = false)
    private String operationTypeLabel;
    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人姓名
     */
    @TableField(exist = false)
    private String operatorName;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date operationTime;

    /**
     * 操作信息数组形式
     * [{field:"字段名",fieldLabel:"字段中文名称",oldValue:"变更前值",newValue:"变更后值"}]
     */
    private String operationInfo;
} 