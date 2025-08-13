package com.tutu.lease.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.lease.dto.CreateOrderRequest;
import com.tutu.lease.dto.CreateOrderFromGoodsRequest;
import com.tutu.lease.dto.LeaseOrderDto;
import com.tutu.lease.entity.LeaseCart;
import com.tutu.lease.entity.LeaseOrder;
import com.tutu.lease.entity.LeaseOrderItem;
import com.tutu.lease.enums.LeaseOrderStatusEnum;
import com.tutu.lease.enums.LeaseCartStatusEnum;
import com.tutu.lease.mapper.LeaseOrderMapper;
import com.tutu.user.entity.User;
import com.tutu.user.service.UserService;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

    /**
     * 分页查询订单
     * @param page 分页对象
     * @param order 订单对象
     * @return 订单分页对象
     */
    public IPage<LeaseOrder> getOrderPage(Page<LeaseOrder> page,LeaseOrder order) {
        LambdaQueryWrapper<LeaseOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StrUtil.isNotBlank(order.getOrderNo()), LeaseOrder::getOrderNo, order.getOrderNo());
        queryWrapper.eq(StrUtil.isNotBlank(order.getStatus()), LeaseOrder::getStatus, order.getStatus());
        queryWrapper.eq(StrUtil.isNotBlank(order.getUserName()), LeaseOrder::getUserName, order.getUserName());
        return page(page,queryWrapper);
    }

    /**
     * 编辑订单
     * @param orderId 订单ID
     * @param order 订单对象
     * @return 是否成功
     */
    public void editOrder(String orderId,LeaseOrder order) {
        LeaseOrder oldOrder = getById(orderId);
        if (oldOrder == null) {
            throw new ServiceException("订单不存在");
        }
        BeanUtils.copyProperties(order, oldOrder);
        updateById(oldOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrderFromCart(CreateOrderRequest request) {
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
                .eq(LeaseCart::getStatus, LeaseCartStatusEnum.NORMAL.getCode())
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
     
        // 创建订单主表
        LeaseOrder order = new LeaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUserName(user.getUsername());
        order.setStatus(LeaseOrderStatusEnum.PENDING_REVIEW.getCode());
        order.setTotalAmount(totalAmount);
        order.setPaidAmount(BigDecimal.ZERO);
        // 计算订单明细中租赁时间的最大值和最小值
        Date minStartTime = cartItems.stream()
                .map(LeaseCart::getLeaseStartTime)
                .min(Date::compareTo)
                .orElse(new Date());
        Date maxEndTime = cartItems.stream()
                .map(LeaseCart::getLeaseEndTime)
                .max(Date::compareTo)
                .orElse(new Date());
        order.setLeaseStartTime(minStartTime);
        order.setLeaseEndTime(maxEndTime);
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
            item.setRemark(cart.getRemark());
            return item;
        }).collect(Collectors.toList());
        
        leaseOrderItemService.batchSave(orderItems);
        
        // 更新购物车状态为已下单
        cartItems.forEach(cart -> cart.setStatus(LeaseCartStatusEnum.ORDERED.getCode()));
        leaseCartService.updateBatchById(cartItems);
    }

    /**
     * 通过商品信息直接创建订单
     */
    @Transactional(rollbackFor = Exception.class)
    public void createOrderFromGoods(CreateOrderFromGoodsRequest request) {
        String userId = request.getUserId();
        // 获取用户信息
        User user = userService.getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证商品信息
        if (request.getOrderItems().isEmpty()) {
            throw new RuntimeException("商品信息不能为空");
        }
        // 验证每个商品信息
        for (CreateOrderFromGoodsRequest.OrderGoodsInfo goodsInfo : request.getOrderItems()) {
            if (goodsInfo.getLeaseEndTime().before(goodsInfo.getLeaseStartTime())) {
                throw new RuntimeException("租赁结束时间不能早于开始时间");
            }
            // 验证小计金额是否正确
            BigDecimal expectedSubtotal = goodsInfo.getGoodPrice()
                    .multiply(new BigDecimal(goodsInfo.getQuantity()))
                    .multiply(new BigDecimal(goodsInfo.getLeaseDays()));
            if (expectedSubtotal.compareTo(goodsInfo.getSubtotal()) != 0) {
                throw new RuntimeException("商品小计金额计算错误");
            }
        }        
        // 创建订单主表
        LeaseOrder order = new LeaseOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setUserName(user.getUsername());
        order.setStatus(LeaseOrderStatusEnum.PENDING_REVIEW.getCode());
        BeanUtils.copyProperties(request, order);
        
        save(order);
        
        // 创建订单明细
        List<LeaseOrderItem> orderItems = request.getOrderItems().stream().map(goodsInfo -> {
            LeaseOrderItem item = new LeaseOrderItem();
            BeanUtils.copyProperties(goodsInfo, item);
            item.setOrderId(order.getId());
            return item;
        }).collect(Collectors.toList());
        
        leaseOrderItemService.batchSave(orderItems);
    }

    
    public List<LeaseOrderDto> getUserOrderList(String userId, String status) {
        List<LeaseOrderDto> list = baseMapper.selectOrderDetailsByUserId(userId, status);
        list.forEach(item -> {
            LeaseOrderStatusEnum statusEnum = LeaseOrderStatusEnum.getByCode(item.getStatus());
            if (statusEnum != null) {
                item.setStatusDesc(statusEnum.getDescription());
            }
        });
        return list;
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

    


    /**
     * 审核订单
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean reviewOrder(String orderId, boolean approved) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.PENDING_REVIEW.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法审核");
        }
        
        if (approved) {
            order.setStatus(LeaseOrderStatusEnum.LEASING.getCode());
            // order.setReviewTime(new Date()); // 需要实体类添加此字段
        } else {
            // 审核不通过，可以设置为已完成状态或者添加一个审核不通过状态
            order.setStatus(LeaseOrderStatusEnum.COMPLETED.getCode());
            // order.setReviewTime(new Date()); // 需要实体类添加此字段
        }
        
        return updateById(order);
    }

    /**
     * 完成租赁，准备开票
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean finishLeasing(String orderId) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.LEASING.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法完成租赁");
        }
        
        order.setStatus(LeaseOrderStatusEnum.PENDING_INVOICE.getCode());
        // order.setFinishLeaseTime(new Date()); // 需要实体类添加此字段
        
        return updateById(order);
    }

    /**
     * 开票完成
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean completeInvoice(String orderId) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!LeaseOrderStatusEnum.PENDING_INVOICE.getCode().equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，无法完成开票");
        }
        
        order.setStatus(LeaseOrderStatusEnum.COMPLETED.getCode());
        // order.setInvoiceTime(new Date()); // 需要实体类添加此字段
        
        return updateById(order);
    }

    /**
     * 取消订单
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(String orderId, String cancelReason) {
        LeaseOrder order = getById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 只有待审核状态可以取消
        if (!LeaseOrderStatusEnum.PENDING_REVIEW.getCode().equals(order.getStatus())) {
            throw new RuntimeException("当前订单状态不允许取消");
        }
        
        order.setStatus(LeaseOrderStatusEnum.COMPLETED.getCode());
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
