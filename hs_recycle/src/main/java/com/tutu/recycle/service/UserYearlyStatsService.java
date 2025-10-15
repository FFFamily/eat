package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.recycle.dto.UserYearlyStatsDto;
import com.tutu.recycle.dto.MonthlyStatsDto;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.entity.RecycleInvoice;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.order.RecycleOrderItem;
import com.tutu.recycle.enums.RecycleOrderStatusEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private RecycleFundService recycleFundService;

    @Autowired
    private RecycleInvoiceService recycleInvoiceService;

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
        // 计算年份的开始和结束时间
        Date startTime = getYearStartTime(year);
        Date endTime = getYearEndTime(year);
        // 统计合同数量
        calculateContractStats(stats, userId, startTime, endTime);
        // 统计采购订单（已结算状态）
        calculatePurchaseOrderStats(stats, userId, startTime, endTime);
        // 统计发票
        calculateInvoiceStats(stats, userId, startTime, endTime);
        // 统计月度数据
        calculateMonthlyStats(stats, userId, year);
        return stats;
    }

    /**
     * 统计发票
     */
    private void calculateInvoiceStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleInvoice> wrapper = new LambdaQueryWrapper<RecycleInvoice>()
                .eq(RecycleInvoice::getInvoiceAccountId, userId)
                .between(RecycleInvoice::getCreateTime, startTime, endTime);

        List<RecycleInvoice> invoices = recycleInvoiceService.list(wrapper);
        // 统计已开票的金额
        BigDecimal totalAmount = invoices.stream()
                .map(RecycleInvoice::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setSettledInvoiceAmount(totalAmount);
        // 统计未开票的金额
        totalAmount = invoices.stream()
                .map(RecycleInvoice::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setNotSettledInvoiceAmount(totalAmount);

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

        stats.setOrderAmount(totalAmount);
        stats.setPurchaseOrderCount(orders.size());
        if (orders.isEmpty()){
            return;
        }
        // 统计对应订单的货物明细重量
        List<RecycleOrderItem> orderItems = recycleOrderItemService.list(new LambdaQueryWrapper<RecycleOrderItem>()
                .in(RecycleOrderItem::getRecycleOrderId, orders.stream().map(RecycleOrder::getId).toList()));
        BigDecimal totalWeight = orderItems.stream()
                .map(RecycleOrderItem::getGoodWeight)
                .filter(weight -> weight != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalWeight(totalWeight);
        // 统计订单明细中评级调价金额
        BigDecimal totalRatingAdjustAmount = orderItems.stream()
                .map(RecycleOrderItem::getRatingAdjustAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setRatingAdjustAmount(totalRatingAdjustAmount);
        // 统计对应订单走款中的货款金额之和
        List<RecycleFund> funds = recycleFundService.list(new LambdaQueryWrapper<RecycleFund>()
                .in(RecycleFund::getOrderId, orders.stream().map(RecycleOrder::getId).toList()));
        BigDecimal totalFundAmount = funds.stream()
                .map(RecycleFund::getFundAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setOrderFundAmount(totalFundAmount);
        // 对应订单走款中的走款金额之和
        BigDecimal totalFundFlowAmount = funds.stream()
                .map(RecycleFund::getFundFlowAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setFundFlowAmount(totalFundFlowAmount);
        
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
     * 统计回收合同数量
     */
    private void calculateContractStats(UserYearlyStatsDto stats, String userId, Date startTime, Date endTime) {
        LambdaQueryWrapper<RecycleContract> wrapper = new LambdaQueryWrapper<RecycleContract>()
                .eq(RecycleContract::getPartner, userId)
                .between(RecycleContract::getCreateTime, startTime, endTime);

        List<RecycleContract> contracts = recycleContractService.list(wrapper);
        stats.setContractCount(contracts.size());
    }

    /**
     * 统计月度数据
     */
    private void calculateMonthlyStats(UserYearlyStatsDto stats, String userId, Integer year) {
        List<MonthlyStatsDto> monthlyStatsList = new ArrayList<>();
        
        // 初始化12个月的数据
        for (int month = 1; month <= 12; month++) {
            monthlyStatsList.add(new MonthlyStatsDto(month));
        }
        
        // 获取该年度所有已结算的采购订单
        Date yearStartTime = getYearStartTime(year);
        Date yearEndTime = getYearEndTime(year);
        
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<RecycleOrder>()
                .eq(RecycleOrder::getContractPartner, userId)
                .eq(RecycleOrder::getType, RecycleOrderTypeEnum.PURCHASE.getCode())
                .eq(RecycleOrder::getStatus, RecycleOrderStatusEnum.COMPLETED.getCode())
                .between(RecycleOrder::getCreateTime, yearStartTime, yearEndTime);

        List<RecycleOrder> orders = recycleOrderService.list(wrapper);
        
        // 按月份分组统计
        Map<Integer, List<RecycleOrder>> ordersByMonth = orders.stream()
                .collect(Collectors.groupingBy(order -> {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(order.getCreateTime());
                    return cal.get(Calendar.MONTH) + 1; // Calendar.MONTH 从0开始，所以+1
                }));
        
        // 统计每个月的重量和结算收益
        for (Map.Entry<Integer, List<RecycleOrder>> entry : ordersByMonth.entrySet()) {
            Integer month = entry.getKey();
            List<RecycleOrder> monthOrders = entry.getValue();
            
            MonthlyStatsDto monthlyStats = monthlyStatsList.get(month - 1); // 数组索引从0开始
            
            // 统计当月订单数量
            monthlyStats.setOrderCount(monthOrders.size());
            
            // 统计当月结算收益
            BigDecimal monthSettlementAmount = monthOrders.stream()
                    .map(RecycleOrder::getTotalAmount)
                    .filter(amount -> amount != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            monthlyStats.setSettlementAmount(monthSettlementAmount);
            
            // 获取当月订单的明细
            List<String> monthOrderIds = monthOrders.stream()
                    .map(RecycleOrder::getId)
                    .collect(Collectors.toList());
            
            if (!monthOrderIds.isEmpty()) {
                List<RecycleOrderItem> monthOrderItems = recycleOrderItemService.list(
                        new LambdaQueryWrapper<RecycleOrderItem>()
                                .in(RecycleOrderItem::getRecycleOrderId, monthOrderIds));
                
                // 统计当月重量
                BigDecimal monthWeight = monthOrderItems.stream()
                        .map(RecycleOrderItem::getGoodWeight)
                        .filter(weight -> weight != null)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                monthlyStats.setWeight(monthWeight);
            }
        }
        
        stats.setMonthlyStats(monthlyStatsList);
    }
}