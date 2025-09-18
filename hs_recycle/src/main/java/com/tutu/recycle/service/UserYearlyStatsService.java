package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.recycle.dto.UserYearlyStatsDto;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.entity.RecycleOrderItem;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 用户年度统计服务
 */
@Service
public class UserYearlyStatsService {

    @Autowired
    private RecycleOrderService recycleOrderService;
    
    @Autowired
    private RecycleContractService recycleContractService;
    
    @Autowired
    private RecycleOrderItemService recycleOrderItemService;

    /**
     * 获取用户年度统计汇总
     * @param userId 用户ID
     * @param year 统计年份，如果为空则使用当前年份
     * @return 年度统计汇总信息
     */
    public UserYearlyStatsDto getUserYearlyStats(String userId, Integer year) {
        // 如果年份为空，使用当前年份
        if (year == null) {
            year = LocalDate.now().getYear();
        }

        UserYearlyStatsDto stats = new UserYearlyStatsDto();
        stats.setUserId(userId);
        stats.setYear(year);
        stats.setGenerateTime(new Date());

        // 计算年份的开始和结束时间
        Date startTime = getYearStartTime(year);
        Date endTime = getYearEndTime(year);

        // 统计采购订单（已结算状态）
        calculatePurchaseOrderStats(stats, userId, startTime, endTime);

        // 统计运输订单
        calculateTransportOrderStats(stats, userId, startTime, endTime);

        // 统计加工订单
        calculateProcessingOrderStats(stats, userId, startTime, endTime);

        // 统计仓储订单
        calculateStorageOrderStats(stats, userId, startTime, endTime);

        // 统计销售订单
        calculateSalesOrderStats(stats, userId, startTime, endTime);

        // 统计其他订单
        calculateOtherOrderStats(stats, userId, startTime, endTime);

        // 统计采购合同数量
        calculatePurchaseContractStats(stats, userId, startTime, endTime);

        // 统计所有采购订单数量
        calculateAllPurchaseOrderStats(stats, userId, startTime, endTime);

        // 统计采购订单已结算订单的合计重量
        calculateSettledPurchaseOrderWeight(stats, userId, startTime, endTime);

        // 计算总收益和总订单数
        calculateTotals(stats);

        return stats;
    }

    /**
     * 统计采购订单（已结算状态）
     */
    private void calculatePurchaseOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId) // 假设userId对应合同合作方
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PURCHASE.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode()) // 已结算状态
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(RecycleOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setSettlementIncome(totalAmount);
        stats.setPurchaseOrderCount(orders.size());
    }

    /**
     * 统计运输订单
     */
    private void calculateTransportOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.TRANSPORT.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(RecycleOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setTransportIncome(totalAmount);
        stats.setTransportOrderCount(orders.size());
    }

    /**
     * 统计加工订单
     */
    private void calculateProcessingOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PROCESSING.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(RecycleOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setProcessingIncome(totalAmount);
        stats.setProcessingOrderCount(orders.size());
    }

    /**
     * 统计仓储订单
     */
    private void calculateStorageOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.STORAGE.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(RecycleOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setStorageIncome(totalAmount);
        stats.setStorageOrderCount(orders.size());
    }

    /**
     * 统计销售订单
     */
    private void calculateSalesOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.SALE.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(RecycleOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setSalesIncome(totalAmount);
        stats.setSalesOrderCount(orders.size());
    }

    /**
     * 统计其他订单
     */
    private void calculateOtherOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.OTHER.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        BigDecimal totalAmount = orders.stream()
                .map(RecycleOrder::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setOtherIncome(totalAmount);
        stats.setOtherOrderCount(orders.size());
    }

    /**
     * 计算总收益和总订单数
     */
    private void calculateTotals(UserYearlyStatsDto stats) {
        // 计算总收益
        BigDecimal totalIncome = BigDecimal.ZERO;
        if (stats.getSettlementIncome() != null) {
            totalIncome = totalIncome.add(stats.getSettlementIncome());
        }
        if (stats.getTransportIncome() != null) {
            totalIncome = totalIncome.add(stats.getTransportIncome());
        }
        if (stats.getProcessingIncome() != null) {
            totalIncome = totalIncome.add(stats.getProcessingIncome());
        }
        if (stats.getStorageIncome() != null) {
            totalIncome = totalIncome.add(stats.getStorageIncome());
        }
        if (stats.getSalesIncome() != null) {
            totalIncome = totalIncome.add(stats.getSalesIncome());
        }
        if (stats.getOtherIncome() != null) {
            totalIncome = totalIncome.add(stats.getOtherIncome());
        }
        stats.setTotalIncome(totalIncome);

        // 计算总订单数
        int totalOrderCount = 0;
        if (stats.getPurchaseOrderCount() != null) {
            totalOrderCount += stats.getPurchaseOrderCount();
        }
        if (stats.getTransportOrderCount() != null) {
            totalOrderCount += stats.getTransportOrderCount();
        }
        if (stats.getProcessingOrderCount() != null) {
            totalOrderCount += stats.getProcessingOrderCount();
        }
        if (stats.getStorageOrderCount() != null) {
            totalOrderCount += stats.getStorageOrderCount();
        }
        if (stats.getSalesOrderCount() != null) {
            totalOrderCount += stats.getSalesOrderCount();
        }
        if (stats.getOtherOrderCount() != null) {
            totalOrderCount += stats.getOtherOrderCount();
        }
        stats.setTotalOrderCount(totalOrderCount);
    }

    /**
     * 获取年份开始时间
     */
    private Date getYearStartTime(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取年份结束时间
     */
    private Date getYearEndTime(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 11, 31, 23, 59, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * 统计采购合同数量
     */
    private void calculatePurchaseContractStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleContract> wrapper = new LambdaQueryWrapper<RecycleContract>()
                .eq(RecycleContract::getPartner, userId)
                .eq(RecycleContract::getType, "purchase") // 假设采购合同类型为"purchase"
                .between(RecycleContract::getCreateTime, startTime, endTime);

        List<RecycleContract> contracts = recycleContractService.list(wrapper);
        stats.setPurchaseContractCount(contracts.size());
    }

    /**
     * 统计所有采购订单数量（所有状态）
     */
    private void calculateAllPurchaseOrderStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PURCHASE.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        stats.setAllPurchaseOrderCount(orders.size());
    }

    /**
     * 统计采购订单已结算订单的合计重量
     */
    private void calculateSettledPurchaseOrderWeight(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        // 先查询已结算的采购订单
        LambdaQueryWrapper<RecycleOrder> orderWrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PURCHASE.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, startTime, endTime);

        List<RecycleOrder> settledOrders = recycleOrderService.list(orderWrapper);
        
        if (settledOrders.isEmpty()) {
            stats.setSettledPurchaseOrderWeight(BigDecimal.ZERO);
            return;
        }

        // 获取这些订单的ID列表
        List<String> orderIds = settledOrders.stream()
                .map(RecycleOrder::getId)
                .toList();

        // 查询这些订单的明细，计算总重量
        LambdaQueryWrapper<RecycleOrderItem> itemWrapper = new LambdaQueryWrapper<RecycleOrderItem>()
                .in(RecycleOrderItem::getRecycleOrderId, orderIds);

        List<RecycleOrderItem> orderItems = recycleOrderItemService.list(itemWrapper);
        
        BigDecimal totalWeight = orderItems.stream()
                .map(RecycleOrderItem::getGoodWeight)
                .filter(weight -> weight != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.setSettledPurchaseOrderWeight(totalWeight);
    }
}