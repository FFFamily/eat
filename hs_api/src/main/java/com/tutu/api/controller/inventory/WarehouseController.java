package com.tutu.api.controller.inventory;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.inventory.entity.Warehouse;
import com.tutu.inventory.service.WarehouseService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 仓库管理Controller
 */
@RestController
@RequestMapping("/inventory/warehouse")
public class WarehouseController {
    
    @Resource
    private WarehouseService warehouseService;
    
    /**
     * 创建仓库
     */
    @PostMapping("/create")
    public BaseResponse<Warehouse> createWarehouse(@RequestBody Warehouse warehouse) {
        return BaseResponse.success(warehouseService.createWarehouse(warehouse));
    }
    
    /**
     * 更新仓库
     */
    @PutMapping("/update")
    public BaseResponse<Warehouse> updateWarehouse(@RequestBody Warehouse warehouse) {
        return BaseResponse.success(warehouseService.updateWarehouse(warehouse));
    }
    
    /**
     * 根据ID查询仓库
     */
    @GetMapping("/get/{id}")
    public BaseResponse<Warehouse> getWarehouse(@PathVariable String id) {
        return BaseResponse.success(warehouseService.getById(id));
    }
    
    /**
     * 删除仓库
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> deleteWarehouse(@PathVariable String id) {
        warehouseService.removeById(id);
        return BaseResponse.success();
    }
    
    /**
     * 启用/停用仓库
     */
    @PutMapping("/updateStatus")
    public BaseResponse<Void> updateWarehouseStatus(@RequestParam String warehouseId, @RequestParam String status) {
        warehouseService.updateWarehouseStatus(warehouseId, status);
        return BaseResponse.success();
    }
    
    /**
     * 分页查询仓库
     */
    @GetMapping("/page")
    public BaseResponse<Page<Warehouse>> pageWarehouse(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String warehouseName,
            @RequestParam(required = false) String status) {
        return BaseResponse.success(warehouseService.pageWarehouse(page, size, warehouseName, status));
    }
    
    /**
     * 获取所有启用的仓库
     */
    @GetMapping("/listActive")
    public BaseResponse<List<Warehouse>> listActiveWarehouses() {
        return BaseResponse.success(warehouseService.listActiveWarehouses());
    }
}

