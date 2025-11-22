package com.tutu.inventory.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.inventory.dto.InventoryInRequest;
import com.tutu.inventory.entity.InventoryIn;
import com.tutu.inventory.entity.InventoryInItem;
import com.tutu.inventory.enums.InventoryBusinessTypeEnum;
import com.tutu.inventory.enums.InventoryStatusEnum;
import com.tutu.inventory.enums.InventoryTransactionTypeEnum;
import com.tutu.inventory.mapper.InventoryInMapper;

import com.tutu.recycle.service.RecycleOrderService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 入库管理服务
 */
@Service
public class InventoryInService extends ServiceImpl<InventoryInMapper, InventoryIn> {
    
    @Resource
    private InventoryInItemService inventoryInItemService;
    
    @Resource
    private InventoryService inventoryService;
    
    @Resource
    private InventoryTransactionService inventoryTransactionService;
    
    @Resource
    private WarehouseService warehouseService;

    @Resource
    private RecycleOrderService recycleOrderService;
    
    /**
     * 创建入库单
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryIn createInventoryIn(InventoryInRequest request, String operatorId, String operatorName) {
        if (CollUtil.isEmpty(request.getItems())) {
            throw new ServiceException("入库明细不能为空");
        }
        
        // 创建入库单
        InventoryIn inventoryIn = new InventoryIn();
        inventoryIn.setInNo(generateInNo());
        inventoryIn.setWarehouseId(request.getWarehouseId());
        inventoryIn.setInType(request.getInType());
        inventoryIn.setSourceOrderId(request.getSourceOrderId());
        inventoryIn.setSourceOrderNo(request.getSourceOrderNo());
        inventoryIn.setOperatorId(operatorId);
        inventoryIn.setOperatorName(operatorName);
        inventoryIn.setStatus(InventoryStatusEnum.PENDING.getCode());
        inventoryIn.setRemark(request.getRemark());
        
        // 计算总数量
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (InventoryInRequest.InventoryInItemRequest itemRequest : request.getItems()) {
            totalQuantity = totalQuantity.add(new BigDecimal(itemRequest.getInQuantity()));
        }
        inventoryIn.setTotalQuantity(totalQuantity);
        
        save(inventoryIn);
        
        // 创建入库明细
        for (InventoryInRequest.InventoryInItemRequest itemRequest : request.getItems()) {
            InventoryInItem item = new InventoryInItem();
            item.setInId(inventoryIn.getId());
            item.setGoodId(itemRequest.getGoodId());
            item.setGoodNo(itemRequest.getGoodNo());
            item.setGoodName(itemRequest.getGoodName());
            item.setGoodType(itemRequest.getGoodType());
            item.setGoodModel(itemRequest.getGoodModel());
            item.setInQuantity(new BigDecimal(itemRequest.getInQuantity()));
            item.setUnit(itemRequest.getUnit());
            item.setRemark(itemRequest.getRemark());
            inventoryInItemService.save(item);
        }
        
        return inventoryIn;
    }
    
    /**
     * 确认入库
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmInventoryIn(String inId) {
        InventoryIn inventoryIn = getById(inId);
        if (inventoryIn == null) {
            throw new ServiceException("入库单不存在");
        }
        
        if (!InventoryStatusEnum.PENDING.getCode().equals(inventoryIn.getStatus())) {
            throw new ServiceException("入库单状态不正确，无法确认入库");
        }
        
        // 获取入库明细
        LambdaQueryWrapper<InventoryInItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryInItem::getInId, inId);
        List<InventoryInItem> items = inventoryInItemService.list(wrapper);
        
        if (CollUtil.isEmpty(items)) {
            throw new ServiceException("入库明细不存在");
        }
        
        // 更新库存
        for (InventoryInItem item : items) {
            // 增加库存
            inventoryService.increaseStock(
                inventoryIn.getWarehouseId(),
                item.getGoodNo(),
                item.getGoodName(),
                item.getGoodType(),
                item.getGoodModel(),
                item.getInQuantity(),
                item.getUnit()
            );
            
            // 记录库存流水
            inventoryTransactionService.recordTransaction(
                inventoryIn.getWarehouseId(),
                item.getGoodId(),
                item.getGoodNo(),
                item.getGoodName(),
                InventoryTransactionTypeEnum.IN.getCode(),
                getBusinessType(inventoryIn.getInType()),
                item.getInQuantity(),
                inventoryIn.getInNo(),
                inventoryIn.getOperatorId(),
                inventoryIn.getOperatorName()
            );
        }
        
        // 更新入库单状态
        inventoryIn.setStatus(InventoryStatusEnum.COMPLETED.getCode());
        inventoryIn.setInTime(new Date());
        updateById(inventoryIn);
    }
    
    /**
     * 取消入库单
     */
    public void cancelInventoryIn(String inId) {
        InventoryIn inventoryIn = getById(inId);
        if (inventoryIn == null) {
            throw new ServiceException("入库单不存在");
        }
        
        if (!InventoryStatusEnum.PENDING.getCode().equals(inventoryIn.getStatus())) {
            throw new ServiceException("只能取消待入库的单据");
        }
        
        inventoryIn.setStatus(InventoryStatusEnum.CANCELLED.getCode());
        updateById(inventoryIn);
    }
    
    /**
     * 分页查询入库单
     */
    public Page<InventoryIn> pageInventoryIn(Integer page, Integer size, String warehouseId, 
                                            String inType, String status, String inNo) {
        Page<InventoryIn> pageParam = new Page<>(page, size);
        return baseMapper.pageInventoryInWithWarehouse(pageParam, warehouseId, inType, status, inNo);
    }

    /**
     * 根据ID获取入库单详情（包含仓库名称）
     */
    public InventoryIn getDetailById(String id) {
        InventoryIn inventoryIn = getById(id);
        if (inventoryIn == null) {
            return null;
        }
        if (inventoryIn.getWarehouseId() != null) {
            var warehouse = warehouseService.getById(inventoryIn.getWarehouseId());
            if (warehouse != null) {
                inventoryIn.setWarehouseName(warehouse.getWarehouseName());
            }
        }
        return inventoryIn;
    }
    
    /**
     * 生成入库单号
     */
    private String generateInNo() {
        return "IN" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 4).toUpperCase();
    }
    
    /**
     * 根据入库类型获取业务类型
     */
    private String getBusinessType(String inType) {
        return switch (inType) {
            case "purchase" -> InventoryBusinessTypeEnum.PURCHASE.getCode();
            case "return" -> InventoryBusinessTypeEnum.RETURN.getCode();
            case "transfer" -> InventoryBusinessTypeEnum.TRANSFER.getCode();
            default -> InventoryBusinessTypeEnum.OTHER.getCode();
        };
    }
}

