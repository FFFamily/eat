package com.tutu.inventory.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.inventory.entity.Warehouse;
import com.tutu.inventory.enums.WarehouseStatusEnum;
import com.tutu.inventory.mapper.WarehouseMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 仓库管理服务
 */
@Service
public class WarehouseService extends ServiceImpl<WarehouseMapper, Warehouse> {
    
    /**
     * 创建仓库
     */
    public Warehouse createWarehouse(Warehouse warehouse) {
        // 校验仓库编号唯一性
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getWarehouseNo, warehouse.getWarehouseNo());
        if (count(wrapper) > 0) {
            throw new ServiceException("仓库编号已存在");
        }
        
        // 设置默认状态
        if (StrUtil.isBlank(warehouse.getStatus())) {
            warehouse.setStatus(WarehouseStatusEnum.ACTIVE.getCode());
        }
        
        save(warehouse);
        return warehouse;
    }
    
    /**
     * 更新仓库
     */
    public Warehouse updateWarehouse(Warehouse warehouse) {
        if (StrUtil.isBlank(warehouse.getId())) {
            throw new ServiceException("仓库ID不能为空");
        }
        
        Warehouse existing = getById(warehouse.getId());
        if (existing == null) {
            throw new ServiceException("仓库不存在");
        }
        
        // 如果修改了仓库编号，需要校验唯一性
        if (!existing.getWarehouseNo().equals(warehouse.getWarehouseNo())) {
            LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Warehouse::getWarehouseNo, warehouse.getWarehouseNo());
            wrapper.ne(Warehouse::getId, warehouse.getId());
            if (count(wrapper) > 0) {
                throw new ServiceException("仓库编号已存在");
            }
        }
        
        updateById(warehouse);
        return warehouse;
    }
    
    /**
     * 启用/停用仓库
     */
    public void updateWarehouseStatus(String warehouseId, String status) {
        Warehouse warehouse = getById(warehouseId);
        if (warehouse == null) {
            throw new ServiceException("仓库不存在");
        }
        
        warehouse.setStatus(status);
        updateById(warehouse);
    }
    
    /**
     * 分页查询仓库
     */
    public Page<Warehouse> pageWarehouse(Integer page, Integer size, String warehouseName, String status) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(warehouseName)) {
            wrapper.like(Warehouse::getWarehouseName, warehouseName);
        }
        if (StrUtil.isNotBlank(status)) {
            wrapper.eq(Warehouse::getStatus, status);
        }
        
        wrapper.orderByDesc(Warehouse::getCreateTime);
        
        return page(new Page<>(page, size), wrapper);
    }
    
    /**
     * 获取所有启用的仓库
     */
    public List<Warehouse> listActiveWarehouses() {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Warehouse::getStatus, WarehouseStatusEnum.ACTIVE.getCode());
        wrapper.orderByAsc(Warehouse::getWarehouseName);
        return list(wrapper);
    }
}

