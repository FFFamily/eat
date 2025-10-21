package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.inventory.entity.InventoryOut;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库单Mapper
 */
@Mapper
public interface InventoryOutMapper extends BaseMapper<InventoryOut> {
}

