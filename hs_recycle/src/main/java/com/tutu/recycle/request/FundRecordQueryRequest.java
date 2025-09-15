package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 走款记录查询请求
 */
@Getter
@Setter
public class FundRecordQueryRequest {
    
    /**
     * 走款状态（可为空或"all"表示查询所有状态）
     */
    private String status;
}