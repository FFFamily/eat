package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.inventory.entity.InventoryOut;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 出库单Mapper
 */
@Mapper
public interface InventoryOutMapper extends BaseMapper<InventoryOut> {
    
    /**
     * 分页查询出库单（带仓库名称）
     * @param page 分页对象
     * @param warehouseId 仓库ID
     * @param outType 出库类型
     * @param status 状态
     * @param outNo 出库单号
     * @return 出库单分页数据
     */
    Page<InventoryOut> pageInventoryOutWithWarehouse(
            Page<InventoryOut> page,
            @Param("warehouseId") String warehouseId,
            @Param("outType") String outType,
            @Param("status") String status,
            @Param("outNo") String outNo
    );
}

