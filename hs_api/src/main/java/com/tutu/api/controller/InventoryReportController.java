package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.InventoryReportDto;
import com.tutu.recycle.service.InventoryReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
     * 获取库存统计报表（不分页）
     * @return 库存统计报表列表
     */
    @GetMapping("/list/all")
    public BaseResponse<List<InventoryReportDto>> getAllInventoryReport() {
        List<InventoryReportDto> reportList = inventoryReportService.getInventoryReport();
        return BaseResponse.success(reportList);
    }

    /**
     * 根据货物编号获取库存详情
     * @param goodNo 货物编号
     * @return 库存详情
     */
    @GetMapping("/detail/{goodNo}")
    public BaseResponse<InventoryReportDto> getInventoryByGoodNo(@PathVariable String goodNo) {
        InventoryReportDto report = inventoryReportService.getInventoryByGoodNo(goodNo);
        if (report == null) {
            return BaseResponse.error("未找到该货物的库存信息");
        }
        return BaseResponse.success(report);
    }

    /**
     * 获取库存统计汇总
     * @return 汇总信息
     */
    @GetMapping("/summary")
    public BaseResponse<Map<String, Object>> getInventorySummary() {
        Map<String, Object> summary = inventoryReportService.getInventorySummary();
        return BaseResponse.success(summary);
    }

    /**
     * 导出库存统计报表
     * @return 导出结果
     */
    @GetMapping("/export")
    public BaseResponse<String> exportInventoryReport() {
        // 这里可以实现Excel导出功能
        // 暂时返回成功信息
        return BaseResponse.success("导出功能待实现");
    }

    /**
     * 根据货物分类筛选库存报表（分页）
     * @param goodType 货物分类
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @return 分页的筛选后库存报表
     */
    @GetMapping("/filter/type/{goodType}")
    public BaseResponse<IPage<InventoryReportDto>> getInventoryReportByType(
            @PathVariable String goodType,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<InventoryReportDto> reportPage = inventoryReportService.getInventoryReportByType(goodType, page, size);
        return BaseResponse.success(reportPage);
    }

    /**
     * 根据货物分类筛选库存报表（不分页）
     * @param goodType 货物分类
     * @return 筛选后的库存报表
     */
    @GetMapping("/filter/type/{goodType}/all")
    public BaseResponse<List<InventoryReportDto>> getAllInventoryReportByType(@PathVariable String goodType) {
        List<InventoryReportDto> allReports = inventoryReportService.getInventoryReport();
        List<InventoryReportDto> filteredReports = allReports.stream()
                .filter(report -> goodType.equals(report.getGoodType()))
                .toList();
        return BaseResponse.success(filteredReports);
    }

    /**
     * 获取库存预警信息（分页）
     * @param threshold 预警阈值，默认为10
     * @param page 页码，默认为1
     * @param size 每页大小，默认为10
     * @return 分页的库存预警列表
     */
    @GetMapping("/warning")
    public BaseResponse<IPage<InventoryReportDto>> getInventoryWarning(
            @RequestParam(defaultValue = "10") int threshold,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        IPage<InventoryReportDto> warningPage = inventoryReportService.getInventoryWarning(threshold, page, size);
        return BaseResponse.success(warningPage);
    }

    /**
     * 获取库存预警信息（不分页）
     * @param threshold 预警阈值，默认为10
     * @return 库存预警列表
     */
    @GetMapping("/warning/all")
    public BaseResponse<List<InventoryReportDto>> getAllInventoryWarning(@RequestParam(defaultValue = "10") int threshold) {
        List<InventoryReportDto> allReports = inventoryReportService.getInventoryReport();
        List<InventoryReportDto> warningReports = allReports.stream()
                .filter(report -> report.getCurrentStock() < threshold)
                .toList();
        return BaseResponse.success(warningReports);
    }
}