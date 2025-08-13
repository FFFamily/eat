package com.tutu.system.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 合同更新日志DTO
 */
@Getter
@Setter
public class SysContractUpdateLogDto {

    private String id;

    /**
     * 合同ID
     */
    private String contractId;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作类型描述
     */
    private String operationTypeDescription;

    /**
     * 操作人ID
     */
    private String operatorId;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间
     */
    private Date operationTime;

    /**
     * 变更字段名称
     */
    private String fieldName;

    /**
     * 字段中文名称
     */
    private String fieldLabel;

    /**
     * 变更前值
     */
    private String oldValue;

    /**
     * 变更后值
     */
    private String newValue;

    /**
     * 变更原因
     */
    private String changeReason;

    /**
     * 备注
     */
    private String remark;

    /**
     * 操作IP地址
     */
    private String operationIp;

    /**
     * 用户代理（浏览器信息）
     */
    private String userAgent;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
} 