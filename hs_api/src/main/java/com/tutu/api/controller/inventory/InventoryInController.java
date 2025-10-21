package com.tutu.api.controller.inventory;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.inventory.dto.InventoryInRequest;
import com.tutu.inventory.entity.InventoryIn;
import com.tutu.inventory.entity.InventoryInItem;
import com.tutu.inventory.service.InventoryInItemService;
import com.tutu.inventory.service.InventoryInService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 入库管理Controller
 */
@RestController
@RequestMapping("/inventory/in")
public class InventoryInController {
    
    @Resource
    private InventoryInService inventoryInService;
    
    @Resource
    private InventoryInItemService inventoryInItemService;
    
    /**
     * 创建入库单
     */
    @PostMapping("/create")
    public BaseResponse<InventoryIn> createInventoryIn(@RequestBody InventoryInRequest request) {
        String operatorId = StpUtil.getLoginIdAsString();
        // 实际应该从用户信息中获取操作人姓名，这里简化处理
        String operatorName = operatorId;
        return BaseResponse.success(inventoryInService.createInventoryIn(request, operatorId, operatorName));
    }
    
    /**
     * 根据ID查询入库单
     */
    @GetMapping("/get/{id}")
    public BaseResponse<InventoryIn> getInventoryIn(@PathVariable String id) {
        return BaseResponse.success(inventoryInService.getById(id));
    }
    
    /**
     * 查询入库单明细
     */
    @GetMapping("/items/{inId}")
    public BaseResponse<List<InventoryInItem>> getInventoryInItems(@PathVariable String inId) {
        return BaseResponse.success(inventoryInItemService.listByInId(inId));
    }
    
    /**
     * 确认入库
     */
    @PutMapping("/confirm/{inId}")
    public BaseResponse<Void> confirmInventoryIn(@PathVariable String inId) {
        inventoryInService.confirmInventoryIn(inId);
        return BaseResponse.success();
    }
    
    /**
     * 取消入库单
     */
    @PutMapping("/cancel/{inId}")
    public BaseResponse<Void> cancelInventoryIn(@PathVariable String inId) {
        inventoryInService.cancelInventoryIn(inId);
        return BaseResponse.success();
    }
    
    /**
     * 分页查询入库单
     */
    @GetMapping("/page")
    public BaseResponse<Page<InventoryIn>> pageInventoryIn(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String warehouseId,
            @RequestParam(required = false) String inType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String inNo) {
        return BaseResponse.success(inventoryInService.pageInventoryIn(page, size, warehouseId, inType, status, inNo));
    }
}

