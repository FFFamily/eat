package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 分页请求
 */
@Getter
@Setter
public class PageRequest {
    
    /**
     * 当前页
     */
    private Integer current = 1;
    
    /**
     * 每页大小
     */
    private Integer size = 10;
}

