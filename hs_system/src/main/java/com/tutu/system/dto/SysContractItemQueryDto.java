package com.tutu.system.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 合同子项查询请求DTO
 */
@Getter
@Setter
public class SysContractItemQueryDto {
    
    /**
     * 合同ID
     */
    private String contractId;
    
    /**
     * 回收货物名称（模糊查询）
     */
    private String recycleGoodName;
    
    /**
     * 租赁设备名称（模糊查询）
     */
    private String leaseGoodName;
    
    /**
     * 回收货物ID
     */
    private String recycleGoodId;
    
    /**
     * 租赁设备ID
     */
    private String leaseGoodId;
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
} 