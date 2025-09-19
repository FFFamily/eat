package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 回收订单查询请求DTO
 */
@Getter
@Setter
public class RecycleOrderQueryRequest {
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页数量
     */
    private Integer size = 10;
    
    /**
     * 订单类型
     */
    private String type;
    
    /**
     * 订单状态
     */
    private String status;
    
    /**
     * 订单识别码
     */
    private String identifyCode;
    
    /**
     * 合作方名称
     */
    private String contractPartner;
}
