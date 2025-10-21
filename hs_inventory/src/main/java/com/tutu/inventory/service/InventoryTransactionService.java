package com.tutu.inventory.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.inventory.entity.Inventory;
import com.tutu.inventory.entity.InventoryTransaction;
import com.tutu.inventory.mapper.InventoryTransactionMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 库存流水服务
 */
@Service
public class InventoryTransactionService extends ServiceImpl<InventoryTransactionMapper, InventoryTransaction> {
    
    @Resource
    private InventoryService inventoryService;
    
    /**
     * 记录库存流水
     */
    public void recordTransaction(String warehouseId, String goodNo, String goodName,
                                  String transactionType, String businessType, BigDecimal quantity,
                                  String relatedNo, String operatorId, String operatorName) {
        // 获取当前库存
        Inventory inventory = inventoryService.getInventory(warehouseId, goodNo);
        
        BigDecimal beforeStock = BigDecimal.ZERO;
        BigDecimal afterStock = BigDecimal.ZERO;
        
        if (inventory != null) {
            afterStock = inventory.getCurrentStock();
            // 计算变动前库存
            beforeStock = afterStock.subtract(quantity);
        }
        
        // 创建流水记录
        InventoryTransaction transaction = new InventoryTransaction();
        transaction.setTransactionNo(generateTransactionNo());
        transaction.setWarehouseId(warehouseId);
        transaction.setGoodNo(goodNo);
        transaction.setGoodName(goodName);
        transaction.setTransactionType(transactionType);
        transaction.setBusinessType(businessType);
        transaction.setQuantity(quantity);
        transaction.setBeforeStock(beforeStock);
        transaction.setAfterStock(afterStock);
        transaction.setRelatedNo(relatedNo);
        transaction.setTransactionTime(new Date());
        transaction.setOperatorId(operatorId);
        transaction.setOperatorName(operatorName);
        
        save(transaction);
    }
    
    /**
     * 分页查询库存流水
     */
    public Page<InventoryTransaction> pageTransaction(Integer page, Integer size, String warehouseId,
                                                     String goodNo, String transactionType, String businessType) {
        LambdaQueryWrapper<InventoryTransaction> wrapper = new LambdaQueryWrapper<>();
        
        if (StrUtil.isNotBlank(warehouseId)) {
            wrapper.eq(InventoryTransaction::getWarehouseId, warehouseId);
        }
        if (StrUtil.isNotBlank(goodNo)) {
            wrapper.eq(InventoryTransaction::getGoodNo, goodNo);
        }
        if (StrUtil.isNotBlank(transactionType)) {
            wrapper.eq(InventoryTransaction::getTransactionType, transactionType);
        }
        if (StrUtil.isNotBlank(businessType)) {
            wrapper.eq(InventoryTransaction::getBusinessType, businessType);
        }
        
        wrapper.orderByDesc(InventoryTransaction::getTransactionTime);
        
        return page(new Page<>(page, size), wrapper);
    }
    
    /**
     * 生成流水号
     */
    private String generateTransactionNo() {
        return "TXN" + System.currentTimeMillis() + IdUtil.randomUUID().substring(0, 4).toUpperCase();
    }
}

