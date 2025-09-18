package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.recycle.dto.InventoryReportDto;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.entity.RecycleOrderItem;
import com.tutu.recycle.mapper.RecycleOrderItemMapper;
import com.tutu.recycle.mapper.RecycleOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 库存统计报表服务
 */
@Service
public class InventoryReportService {

    @Autowired
    private RecycleOrderMapper recycleOrderMapper;
    
    @Autowired
    private RecycleOrderItemMapper recycleOrderItemMapper;

    /**
     * 获取库存统计报表（分页）
     * @param page 页码
     * @param size 每页大小
     * @return 分页的库存统计报表
     */
    public IPage<InventoryReportDto> getInventoryReport(int page, int size) {
        List<InventoryReportDto> allReports = getInventoryReport();
        
        // 手动分页
        int total = allReports.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<InventoryReportDto> pageData = allReports.subList(start, end);
        
        // 创建分页对象
        Page<InventoryReportDto> resultPage = new Page<>(page, size);
        resultPage.setTotal(total);
        resultPage.setRecords(pageData);
        
        return resultPage;
    }

    /**
     * 获取库存统计报表
     * @return 库存统计报表列表
     */
    public List<InventoryReportDto> getInventoryReport() {
        // 获取所有回收订单
        List<RecycleOrder> allOrders = recycleOrderMapper.selectList(null);
        // 获取所有回收订单明细
        List<RecycleOrderItem> allOrderItems = recycleOrderItemMapper.selectList(null);
        // 按货物编号分组统计
        Map<String, List<RecycleOrderItem>> itemsByGoodNo = allOrderItems.stream()
                .collect(Collectors.groupingBy(RecycleOrderItem::getGoodNo));
        List<InventoryReportDto> reportList = new ArrayList<>();
        for (Map.Entry<String, List<RecycleOrderItem>> entry : itemsByGoodNo.entrySet()) {
            List<RecycleOrderItem> items = entry.getValue();
            if (items.isEmpty()) {
                continue;
            }
            InventoryReportDto report = new InventoryReportDto();
            // 设置基本信息（取第一个货物的信息）
            RecycleOrderItem firstItem = items.get(0);
            report.setGoodNo(firstItem.getGoodNo());
            report.setGoodName(firstItem.getGoodName());
            report.setGoodType(firstItem.getGoodType());
            report.setGoodModel(firstItem.getGoodModel());
            // 创建订单ID到订单的映射
            Map<String, RecycleOrder> orderMap = allOrders.stream()
                    .collect(Collectors.toMap(RecycleOrder::getId, order -> order));
            
            // 统计入库和出库
            int totalInQuantity = 0;
            int totalOutQuantity = 0;
            BigDecimal totalInAmount = BigDecimal.ZERO;
            BigDecimal totalOutAmount = BigDecimal.ZERO;
            Date lastInTime = null;
            Date lastOutTime = null;
            Set<String> orderIds = new HashSet<>();
            
            for (RecycleOrderItem item : items) {
                RecycleOrder order = orderMap.get(item.getRecycleOrderId());
                if (order == null) {
                    continue;
                }
                
                orderIds.add(item.getRecycleOrderId());
                
                // 根据流转方向判断入库还是出库
                if ("IN".equals(order.getFlowDirection()) || "入库".equals(order.getFlowDirection())) {
                    // 入库
                    totalInQuantity += item.getGoodCount() != null ? item.getGoodCount() : 0;
                    totalInAmount = totalInAmount.add(item.getGoodTotalPrice() != null ? item.getGoodTotalPrice() : BigDecimal.ZERO);
                    
                    if (lastInTime == null || order.getCreateTime().after(lastInTime)) {
                        lastInTime = order.getCreateTime();
                    }
                } else if ("OUT".equals(order.getFlowDirection()) || "出库".equals(order.getFlowDirection())) {
                    // 出库
                    totalOutQuantity += item.getGoodCount() != null ? item.getGoodCount() : 0;
                    totalOutAmount = totalOutAmount.add(item.getGoodTotalPrice() != null ? item.getGoodTotalPrice() : BigDecimal.ZERO);
                    
                    if (lastOutTime == null || order.getCreateTime().after(lastOutTime)) {
                        lastOutTime = order.getCreateTime();
                    }
                }
            }
            
            // 计算当前库存
            int currentStock = totalInQuantity - totalOutQuantity;
            
            // 计算库存总价值（按平均单价计算）
            BigDecimal averagePrice = BigDecimal.ZERO;
            if (totalInQuantity > 0) {
                averagePrice = totalInAmount.divide(BigDecimal.valueOf(totalInQuantity), 2, RoundingMode.HALF_UP);
            }
            BigDecimal stockValue = averagePrice.multiply(BigDecimal.valueOf(currentStock));
            
            // 设置统计结果
            report.setTotalInQuantity(totalInQuantity);
            report.setTotalOutQuantity(totalOutQuantity);
            report.setCurrentStock(currentStock);
            report.setTotalInAmount(totalInAmount);
            report.setTotalOutAmount(totalOutAmount);
            report.setStockValue(stockValue);
            report.setAveragePrice(averagePrice);
            report.setLastInTime(lastInTime);
            report.setLastOutTime(lastOutTime);
            report.setOrderCount(orderIds.size());
            
            // 设置流转方向统计
            StringBuilder flowStats = new StringBuilder();
            if (totalInQuantity > 0) {
                flowStats.append("入库:").append(totalInQuantity).append("件");
            }
            if (totalOutQuantity > 0) {
                if (flowStats.length() > 0) {
                    flowStats.append(", ");
                }
                flowStats.append("出库:").append(totalOutQuantity).append("件");
            }
            report.setFlowDirectionStats(flowStats.toString());
            
            reportList.add(report);
        }
        
        // 按货物编号排序
        reportList.sort(Comparator.comparing(InventoryReportDto::getGoodNo));
        
        return reportList;
    }

    /**
     * 根据货物编号获取库存详情
     * @param goodNo 货物编号
     * @return 库存详情
     */
    public InventoryReportDto getInventoryByGoodNo(String goodNo) {
        List<InventoryReportDto> allReports = getInventoryReport();
        return allReports.stream()
                .filter(report -> goodNo.equals(report.getGoodNo()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 根据货物分类获取库存统计报表（分页）
     * @param goodType 货物分类
     * @param page 页码
     * @param size 每页大小
     * @return 分页的库存统计报表
     */
    public IPage<InventoryReportDto> getInventoryReportByType(String goodType, int page, int size) {
        List<InventoryReportDto> allReports = getInventoryReport();
        List<InventoryReportDto> filteredReports = allReports.stream()
                .filter(report -> goodType.equals(report.getGoodType()))
                .toList();
        
        // 手动分页
        int total = filteredReports.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<InventoryReportDto> pageData = filteredReports.subList(start, end);
        
        // 创建分页对象
        Page<InventoryReportDto> resultPage = new Page<>(page, size);
        resultPage.setTotal(total);
        resultPage.setRecords(pageData);
        
        return resultPage;
    }

    /**
     * 获取库存预警信息（分页）
     * @param threshold 预警阈值
     * @param page 页码
     * @param size 每页大小
     * @return 分页的库存预警列表
     */
    public IPage<InventoryReportDto> getInventoryWarning(int threshold, int page, int size) {
        List<InventoryReportDto> allReports = getInventoryReport();
        List<InventoryReportDto> warningReports = allReports.stream()
                .filter(report -> report.getCurrentStock() < threshold)
                .toList();
        
        // 手动分页
        int total = warningReports.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        
        List<InventoryReportDto> pageData = warningReports.subList(start, end);
        
        // 创建分页对象
        Page<InventoryReportDto> resultPage = new Page<>(page, size);
        resultPage.setTotal(total);
        resultPage.setRecords(pageData);
        
        return resultPage;
    }

    /**
     * 获取库存统计汇总
     * @return 汇总信息
     */
    public Map<String, Object> getInventorySummary() {
        List<InventoryReportDto> reports = getInventoryReport();
        
        int totalGoodsTypes = reports.size();
        int totalInQuantity = reports.stream().mapToInt(InventoryReportDto::getTotalInQuantity).sum();
        int totalOutQuantity = reports.stream().mapToInt(InventoryReportDto::getTotalOutQuantity).sum();
        int totalCurrentStock = reports.stream().mapToInt(InventoryReportDto::getCurrentStock).sum();
        BigDecimal totalInAmount = reports.stream()
                .map(InventoryReportDto::getTotalInAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalOutAmount = reports.stream()
                .map(InventoryReportDto::getTotalOutAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalStockValue = reports.stream()
                .map(InventoryReportDto::getStockValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalGoodsTypes", totalGoodsTypes);
        summary.put("totalInQuantity", totalInQuantity);
        summary.put("totalOutQuantity", totalOutQuantity);
        summary.put("totalCurrentStock", totalCurrentStock);
        summary.put("totalInAmount", totalInAmount);
        summary.put("totalOutAmount", totalOutAmount);
        summary.put("totalStockValue", totalStockValue);
        return summary;
    }
}