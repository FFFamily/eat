package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.inventory.entity.Warehouse;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库Mapper
 */
@Mapper
public interface WarehouseMapper extends BaseMapper<Warehouse> {
}

