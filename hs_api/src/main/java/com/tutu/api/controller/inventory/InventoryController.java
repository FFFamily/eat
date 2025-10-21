package com.tutu.api.controller.inventory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.inventory.dto.InventoryQueryRequest;
import com.tutu.inventory.dto.InventoryStockWarningVO;
import com.tutu.inventory.dto.InventoryStatisticsVO;
import com.tutu.inventory.entity.Inventory;
import com.tutu.inventory.service.InventoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存查询Controller
 */
@RestController
@RequestMapping("/inventory")
public class InventoryController {
    
    @Resource
    private InventoryService inventoryService;
    
    /**
     * 根据ID查询库存
     */
    @GetMapping("/get/{id}")
    public BaseResponse<Inventory> getInventory(@PathVariable String id) {
        return BaseResponse.success(inventoryService.getById(id));
    }
    
    /**
     * 根据仓库和货物查询库存
     */
    @GetMapping("/getByWarehouseAndGood")
    public BaseResponse<Inventory> getInventoryByWarehouseAndGood(
            @RequestParam String warehouseId,
            @RequestParam String goodNo) {
        return BaseResponse.success(inventoryService.getInventory(warehouseId, goodNo));
    }
    
    /**
     * 分页查询库存
     */
    @PostMapping("/page")
    public BaseResponse<Page<Inventory>> pageInventory(@RequestBody InventoryQueryRequest request) {
        return BaseResponse.success(inventoryService.pageInventory(request));
    }
    
    /**
     * 获取库存预警列表
     */
    @GetMapping("/warning")
    public BaseResponse<List<InventoryStockWarningVO>> getStockWarningList(
            @RequestParam(required = false) String warehouseId) {
        return BaseResponse.success(inventoryService.getStockWarningList(warehouseId));
    }
    
    /**
     * 库存统计
     */
    @GetMapping("/statistics")
    public BaseResponse<InventoryStatisticsVO> getInventoryStatistics(
            @RequestParam(required = false) String warehouseId) {
        return BaseResponse.success(inventoryService.getInventoryStatistics(warehouseId));
    }
    
    /**
     * 设置安全库存
     */
    @PutMapping("/setMinStock")
    public BaseResponse<Void> setMinStock(
            @RequestParam String warehouseId,
            @RequestParam String goodNo,
            @RequestParam BigDecimal minStock) {
        inventoryService.setMinStock(warehouseId, goodNo, minStock);
        return BaseResponse.success();
    }
    
    /**
     * 锁定库存
     */
    @PutMapping("/lockStock")
    public BaseResponse<Void> lockStock(
            @RequestParam String warehouseId,
            @RequestParam String goodNo,
            @RequestParam BigDecimal quantity) {
        inventoryService.lockStock(warehouseId, goodNo, quantity);
        return BaseResponse.success();
    }
    
    /**
     * 解锁库存
     */
    @PutMapping("/unlockStock")
    public BaseResponse<Void> unlockStock(
            @RequestParam String warehouseId,
            @RequestParam String goodNo,
            @RequestParam BigDecimal quantity) {
        inventoryService.unlockStock(warehouseId, goodNo, quantity);
        return BaseResponse.success();
    }
}

