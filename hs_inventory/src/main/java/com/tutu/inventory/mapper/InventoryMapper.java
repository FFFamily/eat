package com.tutu.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.inventory.dto.InventoryQueryRequest;
import com.tutu.inventory.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 库存Mapper
 */
@Mapper
public interface InventoryMapper extends BaseMapper<Inventory> {
    
    /**
     * 分页查询库存（带仓库名称）
     * @param page 分页对象
     * @param request 查询条件
     * @return 库存分页数据
     */
    Page<Inventory> pageInventoryWithWarehouse(Page<Inventory> page, @Param("request") InventoryQueryRequest request);
}

