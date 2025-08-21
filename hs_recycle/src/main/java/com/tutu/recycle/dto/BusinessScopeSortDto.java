package com.tutu.recycle.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 经营范围排序DTO
 */
@Getter
@Setter
public class BusinessScopeSortDto {
    /**
     * 经营范围ID
     */
    private String id;
    
    /**
     * 货物类型
     */
    private String goodType;
    
    /**
     * 排序号
     */
    private Integer sortNum;
    
    /**
     * 操作类型：UP-上移，DOWN-下移
     */
    private String operationType;
} 