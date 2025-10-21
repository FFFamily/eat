package com.tutu.inventory.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.inventory.dto.InventoryQueryRequest;
import com.tutu.inventory.dto.InventoryStockWarningVO;
import com.tutu.inventory.dto.InventoryStatisticsVO;
import com.tutu.inventory.entity.Inventory;
import com.tutu.inventory.entity.Warehouse;
import com.tutu.inventory.mapper.InventoryMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 库存管理服务
 */
@Service
public class InventoryService extends ServiceImpl<InventoryMapper, Inventory> {
    
    @Resource
    private WarehouseService warehouseService;
    
    /**
     * 根据仓库和货物查询库存
     */
    public Inventory getInventory(String warehouseId, String goodNo) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Inventory::getWarehouseId, warehouseId);
        wrapper.eq(Inventory::getGoodNo, goodNo);
        return getOne(wrapper);
    }
    
    /**
     * 增加库存
     */
    public void increaseStock(String warehouseId, String goodNo, String goodName, 
                             String goodType, String goodModel, BigDecimal quantity, String unit) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("增加库存数量必须大于0");
        }
        
        Inventory inventory = getInventory(warehouseId, goodNo);
        
        if (inventory == null) {
            // 新建库存记录
            inventory = new Inventory();
            inventory.setWarehouseId(warehouseId);
            inventory.setGoodNo(goodNo);
            inventory.setGoodName(goodName);
            inventory.setGoodType(goodType);
            inventory.setGoodModel(goodModel);
            inventory.setCurrentStock(quantity);
            inventory.setAvailableStock(quantity);
            inventory.setLockedStock(BigDecimal.ZERO);
            inventory.setUnit(unit);
            inventory.setLastInTime(new Date());
            save(inventory);
        } else {
            // 更新库存
            inventory.setCurrentStock(inventory.getCurrentStock().add(quantity));
            inventory.setAvailableStock(inventory.getAvailableStock().add(quantity));
            inventory.setLastInTime(new Date());
            updateById(inventory);
        }
    }
    
    /**
     * 减少库存
     */
    public void decreaseStock(String warehouseId, String goodNo, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("减少库存数量必须大于0");
        }
        
        Inventory inventory = getInventory(warehouseId, goodNo);
        if (inventory == null) {
            throw new ServiceException("库存不存在");
        }
        
        // 检查可用库存是否充足
        if (inventory.getAvailableStock().compareTo(quantity) < 0) {
            throw new ServiceException("库存不足，当前可用库存：" + inventory.getAvailableStock());
        }
        
        // 更新库存
        inventory.setCurrentStock(inventory.getCurrentStock().subtract(quantity));
        inventory.setAvailableStock(inventory.getAvailableStock().subtract(quantity));
        inventory.setLastOutTime(new Date());
        updateById(inventory);
    }
    
    /**
     * 锁定库存
     */
    public void lockStock(String warehouseId, String goodNo, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("锁定库存数量必须大于0");
        }
        
        Inventory inventory = getInventory(warehouseId, goodNo);
        if (inventory == null) {
            throw new ServiceException("库存不存在");
        }
        
        // 检查可用库存是否充足
        if (inventory.getAvailableStock().compareTo(quantity) < 0) {
            throw new ServiceException("可用库存不足，无法锁定");
        }
        
        // 更新库存
        inventory.setAvailableStock(inventory.getAvailableStock().subtract(quantity));
        inventory.setLockedStock(inventory.getLockedStock().add(quantity));
        updateById(inventory);
    }
    
    /**
     * 解锁库存
     */
    public void unlockStock(String warehouseId, String goodNo, BigDecimal quantity) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("解锁库存数量必须大于0");
        }
        
        Inventory inventory = getInventory(warehouseId, goodNo);
        if (inventory == null) {
            throw new ServiceException("库存不存在");
        }
        
        // 检查锁定库存是否充足
        if (inventory.getLockedStock().compareTo(quantity) < 0) {
            throw new ServiceException("锁定库存不足，无法解锁");
        }
        
        // 更新库存
        inventory.setLockedStock(inventory.getLockedStock().subtract(quantity));
        inventory.setAvailableStock(inventory.getAvailableStock().add(quantity));
        updateById(inventory);
    }
    
    /**
     * 分页查询库存
     */
    public Page<Inventory> pageInventory(InventoryQueryRequest request) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(request.getWarehouseId())) {
            wrapper.eq(Inventory::getWarehouseId, request.getWarehouseId());
        }
        if (StrUtil.isNotBlank(request.getGoodNo())) {
            wrapper.eq(Inventory::getGoodNo, request.getGoodNo());
        }
        if (StrUtil.isNotBlank(request.getGoodName())) {
            wrapper.like(Inventory::getGoodName, request.getGoodName());
        }
        if (StrUtil.isNotBlank(request.getGoodType())) {
            wrapper.eq(Inventory::getGoodType, request.getGoodType());
        }
        
        // 只查询预警库存
        if (request.getWarningOnly() != null && request.getWarningOnly()) {
            wrapper.and(w -> w.le(Inventory::getCurrentStock, 
                w.nested(n -> n.isNotNull(Inventory::getMinStock))));
        }
        
        wrapper.orderByDesc(Inventory::getUpdateTime);
        
        return page(new Page<>(request.getPage(), request.getSize()), wrapper);
    }
    
    /**
     * 获取库存预警列表
     */
    public List<InventoryStockWarningVO> getStockWarningList(String warehouseId) {
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(warehouseId)) {
            wrapper.eq(Inventory::getWarehouseId, warehouseId);
        }
        
        // 查询当前库存低于或等于最小库存的记录
        wrapper.isNotNull(Inventory::getMinStock);
        wrapper.apply("current_stock <= min_stock");
        
        List<Inventory> inventories = list(wrapper);
        List<InventoryStockWarningVO> result = new ArrayList<>();
        
        for (Inventory inventory : inventories) {
            InventoryStockWarningVO vo = new InventoryStockWarningVO();
            BeanUtil.copyProperties(inventory, vo);
            
            // 获取仓库名称
            Warehouse warehouse = warehouseService.getById(inventory.getWarehouseId());
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
            
            // 判断预警类型
            if (inventory.getCurrentStock().compareTo(BigDecimal.ZERO) == 0) {
                vo.setWarningType("无库存");
            } else if (inventory.getCurrentStock().compareTo(inventory.getMinStock()) < 0) {
                vo.setWarningType("低于安全库存");
            } else {
                vo.setWarningType("达到安全库存");
            }
            
            result.add(vo);
        }
        
        return result;
    }
    
    /**
     * 库存统计
     */
    public InventoryStatisticsVO getInventoryStatistics(String warehouseId) {
        InventoryStatisticsVO vo = new InventoryStatisticsVO();
        vo.setWarehouseId(warehouseId);
        
        // 获取仓库信息
        if (StrUtil.isNotBlank(warehouseId)) {
            Warehouse warehouse = warehouseService.getById(warehouseId);
            if (warehouse != null) {
                vo.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        
        LambdaQueryWrapper<Inventory> wrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(warehouseId)) {
            wrapper.eq(Inventory::getWarehouseId, warehouseId);
        }
        
        List<Inventory> inventories = list(wrapper);
        
        // 统计
        int totalGoodsCount = inventories.size();
        int warningGoodsCount = 0;
        BigDecimal totalStock = BigDecimal.ZERO;
        BigDecimal totalAvailableStock = BigDecimal.ZERO;
        BigDecimal totalLockedStock = BigDecimal.ZERO;
        
        for (Inventory inventory : inventories) {
            totalStock = totalStock.add(inventory.getCurrentStock());
            totalAvailableStock = totalAvailableStock.add(inventory.getAvailableStock());
            totalLockedStock = totalLockedStock.add(inventory.getLockedStock());
            
            // 判断是否预警
            if (inventory.getMinStock() != null 
                && inventory.getCurrentStock().compareTo(inventory.getMinStock()) <= 0) {
                warningGoodsCount++;
            }
        }
        
        vo.setTotalGoodsCount(totalGoodsCount);
        vo.setWarningGoodsCount(warningGoodsCount);
        vo.setTotalStock(totalStock);
        vo.setTotalAvailableStock(totalAvailableStock);
        vo.setTotalLockedStock(totalLockedStock);
        
        return vo;
    }
    
    /**
     * 设置安全库存
     */
    public void setMinStock(String warehouseId, String goodNo, BigDecimal minStock) {
        Inventory inventory = getInventory(warehouseId, goodNo);
        if (inventory == null) {
            throw new ServiceException("库存不存在");
        }
        
        inventory.setMinStock(minStock);
        updateById(inventory);
    }
}

