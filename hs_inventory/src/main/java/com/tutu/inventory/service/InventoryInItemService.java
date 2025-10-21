package com.tutu.inventory.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.inventory.entity.InventoryInItem;
import com.tutu.inventory.mapper.InventoryInItemMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 入库明细服务
 */
@Service
public class InventoryInItemService extends ServiceImpl<InventoryInItemMapper, InventoryInItem> {
    
    /**
     * 根据入库单ID查询明细
     */
    public List<InventoryInItem> listByInId(String inId) {
        LambdaQueryWrapper<InventoryInItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryInItem::getInId, inId);
        return list(wrapper);
    }
}

