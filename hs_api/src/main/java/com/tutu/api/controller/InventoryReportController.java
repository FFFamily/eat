package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.InventoryReportDto;
import com.tutu.recycle.service.InventoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 库存统计报表控制器
 */
@RestController
@RequestMapping("/inventory/report")
public class InventoryReportController {

    @Autowired
    private InventoryReportService inventoryReportService;

    /**
     * 获取库存统计报表（分页）
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @return 分页的库存统计报表
     */
    @GetMapping("/list")
    public BaseResponse<IPage<InventoryReportDto>> getInventoryReport(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<InventoryReportDto> reportPage = inventoryReportService.getInventoryReport(page, size);
        return BaseResponse.success(reportPage);
    }
    
    /**
     * 获取库存报表总数
     * @return 库存报表记录总数
     */
    @GetMapping("/count")
    public BaseResponse<Long> getInventoryReportCount() {
        long count = inventoryReportService.getInventoryReportCount();
        return BaseResponse.success(count);
    }
}