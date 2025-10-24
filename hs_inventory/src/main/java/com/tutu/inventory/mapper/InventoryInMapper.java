package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.inventory.entity.InventoryIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 入库单Mapper
 */
@Mapper
public interface InventoryInMapper extends BaseMapper<InventoryIn> {
    
    /**
     * 分页查询入库单（带仓库名称）
     * @param page 分页对象
     * @param warehouseId 仓库ID
     * @param inType 入库类型
     * @param status 状态
     * @param inNo 入库单号
     * @return 入库单分页数据
     */
    Page<InventoryIn> pageInventoryInWithWarehouse(
            Page<InventoryIn> page,
            @Param("warehouseId") String warehouseId,
            @Param("inType") String inType,
            @Param("status") String status,
            @Param("inNo") String inNo
    );
}

