package com.tutu.inventory.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.inventory.dto.InventoryOutRequest;
import com.tutu.inventory.entity.InventoryOut;
import com.tutu.inventory.entity.InventoryOutItem;
import com.tutu.inventory.enums.InventoryBusinessTypeEnum;
import com.tutu.inventory.enums.InventoryStatusEnum;
import com.tutu.inventory.enums.InventoryTransactionTypeEnum;
import com.tutu.inventory.mapper.InventoryOutMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 出库管理服务
 */
@Service
public class InventoryOutService extends ServiceImpl<InventoryOutMapper, InventoryOut> {
    
    @Resource
    private InventoryOutItemService inventoryOutItemService;
    
    @Resource
    private InventoryService inventoryService;
    
    @Resource
    private InventoryTransactionService inventoryTransactionService;
    
    /**
     * 创建出库单
     */
    @Transactional(rollbackFor = Exception.class)
    public InventoryOut createInventoryOut(InventoryOutRequest request, String operatorId, String operatorName) {
        if (CollUtil.isEmpty(request.getItems())) {
            throw new ServiceException("出库明细不能为空");
        }
        
        // 创建出库单
        InventoryOut inventoryOut = new InventoryOut();
        inventoryOut.setOutNo(generateOutNo());
        inventoryOut.setWarehouseId(request.getWarehouseId());
        inventoryOut.setOutType(request.getOutType());
        inventoryOut.setTargetOrderId(request.getTargetOrderId());
        inventoryOut.setTargetOrderNo(request.getTargetOrderNo());
        inventoryOut.setOperatorId(operatorId);
        inventoryOut.setOperatorName(operatorName);
        inventoryOut.setStatus(InventoryStatusEnum.PENDING.getCode());
        inventoryOut.setRemark(request.getRemark());
        
        // 计算总数量
        BigDecimal totalQuantity = BigDecimal.ZERO;
        for (InventoryOutRequest.InventoryOutItemRequest itemRequest : request.getItems()) {
            totalQuantity = totalQuantity.add(new BigDecimal(itemRequest.getOutQuantity()));
        }
        inventoryOut.setTotalQuantity(totalQuantity);
        
        save(inventoryOut);
        
        // 创建出库明细
        for (InventoryOutRequest.InventoryOutItemRequest itemRequest : request.getItems()) {
            InventoryOutItem item = new InventoryOutItem();
            item.setOutId(inventoryOut.getId());
            item.setGoodId(itemRequest.getGoodId());
            item.setGoodNo(itemRequest.getGoodNo());
            item.setGoodName(itemRequest.getGoodName());
            item.setGoodType(itemRequest.getGoodType());
            item.setGoodModel(itemRequest.getGoodModel());
            item.setOutQuantity(new BigDecimal(itemRequest.getOutQuantity()));
            item.setUnit(itemRequest.getUnit());
            item.setRemark(itemRequest.getRemark());
            inventoryOutItemService.save(item);
        }
        
        return inventoryOut;
    }
    
    /**
     * 确认出库
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirmInventoryOut(String outId) {
        InventoryOut inventoryOut = getById(outId);
        if (inventoryOut == null) {
            throw new ServiceException("出库单不存在");
        }
        
        if (!InventoryStatusEnum.PENDING.getCode().equals(inventoryOut.getStatus())) {
            throw new ServiceException("出库单状态不正确，无法确认出库");
        }
        
        // 获取出库明细
        LambdaQueryWrapper<InventoryOutItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(InventoryOutItem::getOutId, outId);
        List<InventoryOutItem> items = inventoryOutItemService.list(wrapper);
        
        if (CollUtil.isEmpty(items)) {
            throw new ServiceException("出库明细不存在");
        }
        
        // 更新库存
        for (InventoryOutItem item : items) {
            // 减少库存
            inventoryService.decreaseStock(
                inventoryOut.getWarehouseId(),
                item.getGoodNo(),
                item.getOutQuantity()
            );
            
            // 记录库存流水（出库数量记为负数）
            inventoryTransactionService.recordTransaction(
                inventoryOut.getWarehouseId(),
                item.getGoodId(),
                item.getGoodNo(),
                item.getGoodName(),
                InventoryTransactionTypeEnum.OUT.getCode(),
                getBusinessType(inventoryOut.getOutType()),
                item.getOutQuantity().negate(),
                inventoryOut.getOutNo(),
                inventoryOut.getOperatorId(),
                inventoryOut.getOperatorName()
            );
        }
        
        // 更新出库单状态
        inventoryOut.setStatus(InventoryStatusEnum.COMPLETED.getCode());
        inventoryOut.setOutTime(new Date());
        updateById(inventoryOut);
    }
    
    /**
     * 取消出库单
     */
    public void cancelInventoryOut(String outId) {
        InventoryOut inventoryOut = getById(outId);
        if (inventoryOut == null) {
            throw new ServiceException("出库单不存在");
        }
        
        if (!InventoryStatusEnum.PENDING.getCode().equals(inventoryOut.getStatus())) {
            throw new ServiceException("只能取消待出库的单据");
        }
        
        inventoryOut.setStatus(InventoryStatusEnum.CANCELLED.getCode());
        updateById(inventoryOut);
    }
    
    /**
     * 分页查询出库单
     */
    public Page<InventoryOut> pageInventoryOut(Integer page, Integer size, String warehouseId, 
                                               String outType, String status, String outNo) {
        Page<InventoryOut> pageParam = new Page<>(page, size);
        return baseMapper.pageInventoryOutWithWarehouse(pageParam, warehouseId, outType, status, outNo);
    }
    
    /**
     * 生成出库单号
     */
    private String generateOutNo() {
        return "OUT" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 4).toUpperCase();
    }
    
    /**
     * 根据出库类型获取业务类型
     */
    private String getBusinessType(String outType) {
        return switch (outType) {
            case "sale" -> InventoryBusinessTypeEnum.SALE.getCode();
            case "loss" -> InventoryBusinessTypeEnum.LOSS.getCode();
            case "transfer" -> InventoryBusinessTypeEnum.TRANSFER.getCode();
            default -> InventoryBusinessTypeEnum.OTHER.getCode();
        };
    }
}

