package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.inventory.entity.InventoryInItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 入库明细Mapper
 */
@Mapper
public interface InventoryInItemMapper extends BaseMapper<InventoryInItem> {
}

