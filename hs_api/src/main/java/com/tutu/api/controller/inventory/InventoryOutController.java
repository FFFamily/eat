package com.tutu.api.controller.inventory;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.inventory.dto.InventoryOutRequest;
import com.tutu.inventory.entity.InventoryOut;
import com.tutu.inventory.entity.InventoryOutItem;
import com.tutu.inventory.service.InventoryOutItemService;
import com.tutu.inventory.service.InventoryOutService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 出库管理Controller
 */
@RestController
@RequestMapping("/inventory/out")
public class InventoryOutController {
    
    @Resource
    private InventoryOutService inventoryOutService;
    
    @Resource
    private InventoryOutItemService inventoryOutItemService;
    
    /**
     * 创建出库单
     */
    @PostMapping("/create")
    public BaseResponse<InventoryOut> createInventoryOut(@RequestBody InventoryOutRequest request) {
        String operatorId = StpUtil.getLoginIdAsString();
        // 实际应该从用户信息中获取操作人姓名，这里简化处理
        String operatorName = operatorId;
        return BaseResponse.success(inventoryOutService.createInventoryOut(request, operatorId, operatorName));
    }
    
    /**
     * 根据ID查询出库单
     */
    @GetMapping("/get/{id}")
    public BaseResponse<InventoryOut> getInventoryOut(@PathVariable String id) {
        return BaseResponse.success(inventoryOutService.getById(id));
    }
    
    /**
     * 查询出库单明细
     */
    @GetMapping("/items/{outId}")
    public BaseResponse<List<InventoryOutItem>> getInventoryOutItems(@PathVariable String outId) {
        return BaseResponse.success(inventoryOutItemService.listByOutId(outId));
    }
    
    /**
     * 确认出库
     */
    @PutMapping("/confirm/{outId}")
    public BaseResponse<Void> confirmInventoryOut(@PathVariable String outId) {
        inventoryOutService.confirmInventoryOut(outId);
        return BaseResponse.success();
    }
    
    /**
     * 取消出库单
     */
    @PutMapping("/cancel/{outId}")
    public BaseResponse<Void> cancelInventoryOut(@PathVariable String outId) {
        inventoryOutService.cancelInventoryOut(outId);
        return BaseResponse.success();
    }
    
    /**
     * 分页查询出库单
     */
    @GetMapping("/page")
    public BaseResponse<Page<InventoryOut>> pageInventoryOut(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) String outType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String outNo) {
        return BaseResponse.success(inventoryOutService.pageInventoryOut(page, size, warehouseId, outType, status, outNo));
    }
}

