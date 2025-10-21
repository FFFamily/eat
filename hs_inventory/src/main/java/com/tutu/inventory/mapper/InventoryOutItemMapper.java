package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.inventory.entity.InventoryOutItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 出库明细Mapper
 */
@Mapper
public interface InventoryOutItemMapper extends BaseMapper<InventoryOutItem> {
}

