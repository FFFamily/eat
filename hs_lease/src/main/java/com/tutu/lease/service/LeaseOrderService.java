package com.tutu.lease.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.lease.dto.CreateOrderRequest;
import com.tutu.lease.dto.LeaseOrderDto;
import com.tutu.lease.entity.LeaseCart;
import com.tutu.lease.entity.LeaseOrder;
import com.tutu.lease.entity.LeaseOrderItem;
import com.tutu.lease.enums.LeaseOrderStatusEnum;
import com.tutu.lease.mapper.LeaseOrderMapper;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 租赁订单服务实现类
 */
@Service
public class LeaseOrderService extends ServiceImpl<LeaseOrderMapper, LeaseOrder> {

    @Autowired
    private LeaseCartService leaseCartService;
    
    @Autowired
    private LeaseOrderItemService leaseOrderItemService;
    
    @Autowired
    private UserService userService;

    
    @Transactional(rollbackFor = Exception.class)
    public LeaseOrderDto createOrderFromCart(CreateOrderRequest request) {
        String userId = StpUtil.getLoginIdAsString();
        
        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 获取购物车商品
        LambdaQueryWrapper<LeaseCart> cartQueryWrapper = new LambdaQueryWrapper<>();
        cartQueryWrapper.in(LeaseCart::getId, request.getCartIds())
                .eq(LeaseCart::getUserId, userId)
                .eq(LeaseCart::getStatus, 0)
                .eq(LeaseCart::getIsDeleted, CommonConstant.NO_STR);
        
        List<LeaseCart> cartItems = leaseCartService.list(cartQueryWrapper);
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车商品不存在或已失效");
        }
        
        if (cartItems.size() != request.getCartIds().size()) {
            throw new RuntimeException("部分购物车商品不存在或已失效");
        }
        
        // 计算订单总金额和租赁时间范围
        BigDecimal totalAmount = cartItems.stream()
                .map(LeaseCart::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        Date minStartTime = cartItems.stream()
                .map(LeaseCart::getLeaseStartTime)
                .min(Date::compareTo)
                .orElse(new Date());
        
        Date maxEndTime = cartItems.stream()
                .map(LeaseCart::getLeaseEndTime)
                .max(Date::compareTo)
                .orElse(new Date());
        
        Integer totalLeaseDays = calculateLeaseDays(minStartTime, maxEndTime);
        
        // 创建订单主表
        LeaseOrder order = new LeaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUserName(user.getNickname());
        order.setUserPhone(user.getPhone());
        order.setStatus(LeaseOrderStatusEnum.PENDING_PAYMENT.getCode());
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        order.setDepositAmount(totalAmount.multiply(new BigDecimal("0.2"))); // 押金为总金额的20%
        order.setLeaseStartTime(minStartTime);
        order.setLeaseEndTime(maxEndTime);
        order.setTotalLeaseDays(totalLeaseDays);
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setReceiverAddress(request.getReceiverAddress());
        order.setReturnAddress(request.getReturnAddress());
        order.setRemark(request.getRemark());
        
        save(order);
        
        // 创建订单明细
        List<LeaseOrderItem> orderItems = cartItems.stream().map(cart -> {
            LeaseOrderItem item = new LeaseOrderItem();
            item.setOrderId(order.getId());
            item.setGoodId(cart.getGoodId());
            item.setGoodName(cart.getGoodName());
            item.setGoodPrice(cart.getGoodPrice());
            item.setQuantity(cart.getQuantity());
            item.setLeaseStartTime(cart.getLeaseStartTime());
            item.setLeaseEndTime(cart.getLeaseEndTime());
            item.setLeaseDays(cart.getLeaseDays());
            item.setSubtotal(cart.getSubtotal());
            item.setDepositAmount(cart.getSubtotal().multiply(new BigDecimal("0.2")));
            item.setStatus(0);
            item.setRemark(cart.getRemark());
            return item;
        }).collect(Collectors.toList());
        
        leaseOrderItemService.batchSave(orderItems);
        
        // 更新购物车状态为已下单
        cartItems.forEach(cart -> cart.setStatus(1));
        leaseCartService.updateBatchById(cartItems);
        
        // 返回订单详情
        return getOrderDetail(order.getId());
    }

    
    public List<LeaseOrderDto> getUserOrderList(String userId, String status) {
        return baseMapper.selectOrderDetailsByUserId(userId, status);
    }

    
    public IPage<LeaseOrderDto> getUserOrderPage(Page<LeaseOrderDto> page, String userId, String status) {
        return baseMapper.selectOrderDetailsPage(page, userId, status);
    }

    
    public LeaseOrderDto getOrderDetail(String orderId) {
        LeaseOrderDto orderDto = baseMapper.selectOrderDetailById(orderId);
        if (orderDto != null) {
            // 设置状态描述
            LeaseOrderStatusEnum statusEnum = LeaseOrderStatusEnum.getByCode(orderDto.getStatus());
            if (statusEnum != null) {
                orderDto.setStatusDesc(statusEnum.getDescription());
            }
            
            // 获取订单明细
            List<LeaseOrderItem> orderItems = leaseOrderItemService.getByOrderId(orderId);
            orderDto.setOrderItems(orderItems);
        }
        return orderDto;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(String orderId) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.PENDING_PAYMENT.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法支付");
        }
        
        order.setStatus(LeaseOrderStatusEnum.PAID.getCode());
        order.setPaidAmount(order.getTotalAmount());
        order.setPayTime(new Date());
        
        return updateById(order);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderId, String cancelReason) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有待支付和已支付状态可以取消
        if (!Arrays.asList(
                LeaseOrderStatusEnum.PENDING_PAYMENT.getCode(),
                LeaseOrderStatusEnum.PAID.getCode()
        ).contains(order.getStatus())) {
            throw new RuntimeException("当前订单状态不允许取消");
        }
        
        order.setStatus(LeaseOrderStatusEnum.CANCELLED.getCode());
        order.setCancelTime(new Date());
        order.setCancelReason(cancelReason);
        
        return updateById(order);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean shipOrder(String orderId, String logisticsCompany, String trackingNumber) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.PAID.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法发货");
        }
        
        order.setStatus(LeaseOrderStatusEnum.SHIPPED.getCode());
        order.setLogisticsCompany(logisticsCompany);
        order.setTrackingNumber(trackingNumber);
        order.setShipTime(new Date());
        
        return updateById(order);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReceive(String orderId) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.SHIPPED.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法确认收货");
        }
        
        order.setStatus(LeaseOrderStatusEnum.LEASING.getCode());
        order.setReceiveTime(new Date());
        
        return updateById(order);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean confirmReturn(String orderId) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!Arrays.asList(
                LeaseOrderStatusEnum.LEASING.getCode(),
                LeaseOrderStatusEnum.LEASE_EXPIRED.getCode()
        ).contains(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法确认归还");
        }
        
        order.setStatus(LeaseOrderStatusEnum.RETURNED.getCode());
        order.setReturnTime(new Date());
        
        return updateById(order);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean completeOrder(String orderId) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.RETURNED.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法完成订单");
        }
        
        order.setStatus(LeaseOrderStatusEnum.COMPLETED.getCode());
        order.setCompleteTime(new Date());
        
        return updateById(order);
    }

    
    public Map<String, Integer> getUserOrderStatusCount(String userId) {
        List<Map<String, Object>> statusCounts = baseMapper.selectOrderStatusCount(userId);
        Map<String, Integer> result = new HashMap<>();
        
        // 初始化所有状态为0
        for (LeaseOrderStatusEnum status : LeaseOrderStatusEnum.values()) {
            result.put(status.getCode(), 0);
        }
        
        // 填充实际数据
        statusCounts.forEach(item -> {
            String status = (String) item.get("status");
            Integer count = ((Number) item.get("count")).intValue();
            result.put(status, count);
        });
        
        return result;
    }

    
    public String generateOrderNo() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomStr = IdUtil.fastSimpleUUID().substring(0, 8).toUpperCase();
        return "LO" + dateStr + randomStr;
    }

    /**
     * 计算租赁天数
     */
    private Integer calculateLeaseDays(Date startTime, Date endTime) {
        LocalDate startDate = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}
