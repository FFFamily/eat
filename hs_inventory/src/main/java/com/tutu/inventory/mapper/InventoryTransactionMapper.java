package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.inventory.entity.InventoryTransaction;
import org.apache.ibatis.annotations.Mapper;

/**
 * 库存流水Mapper
 */
@Mapper
public interface InventoryTransactionMapper extends BaseMapper<InventoryTransaction> {
}

