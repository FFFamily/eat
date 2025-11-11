package com.tutu.recycle.schema;


import java.util.List;

import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.order.RecycleOrderItem;
import com.tutu.recycle.request.recycle_order.SourceCode;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecycleOrderInfo extends RecycleOrder {
    private List<RecycleOrderItem> items;
    private List<SourceCode> sourceCodes;
    
    /**
     * 经办人名称（不映射到数据库）
     */
    private String processorName;
}
