package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.dto.UserOrderInfo;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.response.WxTransportOrderListResponse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户订单 Mapper
 */
@Mapper
public interface UserOrderMapper extends BaseMapper<UserOrder> {

    /**
     * 查询可抢单的用户订单
     * 条件：当前阶段为运输，且不存在运输子订单
     */
    List<WxTransportOrderListResponse> selectAvailableTransportOrders();
}

