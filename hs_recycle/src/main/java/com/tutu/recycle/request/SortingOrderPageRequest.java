package com.tutu.recycle.request;

import lombok.Getter;
import lombok.Setter;

/**
 * 分拣订单分页请求，包含经办人过滤条件
 */
@Getter
@Setter
public class SortingOrderPageRequest extends PageRequest {

    /**
     * 经办人ID
     */
    private String processorId;
}

