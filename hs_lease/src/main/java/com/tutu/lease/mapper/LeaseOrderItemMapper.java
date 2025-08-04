package com.tutu.lease.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.lease.entity.LeaseOrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租赁订单明细Mapper接口
 */
@Mapper
public interface LeaseOrderItemMapper extends BaseMapper<LeaseOrderItem> {
}
