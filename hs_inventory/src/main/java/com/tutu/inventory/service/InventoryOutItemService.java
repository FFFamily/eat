package com.tutu.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.inventory.entity.InventoryOutItem;
import com.tutu.inventory.mapper.InventoryOutItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 出库明细服务
 */
@Service
public class InventoryOutItemService extends ServiceImpl<InventoryOutItemMapper, InventoryOutItem> {
    
    /**
     * 根据出库单ID查询明细
     */
    public List<InventoryOutItem> listByOutId(String outId) {
        LambdaQueryWrapper<InventoryOutItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryOutItem::getOutId, outId);
        return list(wrapper);
    }
}

