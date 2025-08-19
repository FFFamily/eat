package com.tutu.recycle.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.entity.RecycleOrderItem;

/**
 * 回收订单项Mapper
 */
@Mapper
public interface RecycleOrderItemMapper extends BaseMapper<RecycleOrderItem> {

}
