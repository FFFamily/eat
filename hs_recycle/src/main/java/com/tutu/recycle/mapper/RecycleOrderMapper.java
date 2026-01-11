package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.entity.order.RecycleOrder;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecycleOrderMapper extends BaseMapper<RecycleOrder> {
    /**
     * 查询运输订单列表（可按状态、经办人筛选）
     */
    List<RecycleOrder> selectTransportOrders(@Param("transportStatus") String transportStatus,
                                             @Param("processorId") String processorId);

    /**
     * 查询我的待分拣订单列表
     * @param processorId 经办人
     * @return 待分拣订单列表
     */
    List<RecycleOrder> selectMyPendingSortingList(String processorId);
}
