package com.tutu.api.controller.inventory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.inventory.entity.InventoryTransaction;
import com.tutu.inventory.service.InventoryTransactionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 库存流水查询Controller
 */
@RestController
@RequestMapping("/inventory/transaction")
public class InventoryTransactionController {
    
    @Resource
    private InventoryTransactionService inventoryTransactionService;
    
    /**
     * 根据ID查询库存流水
     */
    @GetMapping("/get/{id}")
    public BaseResponse<InventoryTransaction> getTransaction(@PathVariable String id) {
        return BaseResponse.success(inventoryTransactionService.getById(id));
    }
    
    /**
     * 分页查询库存流水
     */
    @GetMapping("/page")
    public BaseResponse<Page<InventoryTransaction>> pageTransaction(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) String goodNo,
            @RequestParam(required = false) String transactionType,
            @RequestParam(required = false) String businessType) {
        return BaseResponse.success(inventoryTransactionService.pageTransaction(
            page, size, warehouseId, goodNo, transactionType, businessType));
    }
}

