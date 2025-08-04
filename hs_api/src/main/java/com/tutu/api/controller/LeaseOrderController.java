package com.tutu.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.dto.CreateOrderRequest;
import com.tutu.lease.dto.LeaseOrderDto;
import com.tutu.lease.service.LeaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 租赁订单控制器
 */
@RestController
@RequestMapping("/lease/order")
public class LeaseOrderController {

    @Autowired
    private LeaseOrderService leaseOrderService;

    /**
     * 从购物车创建订单
     */
    @PostMapping("/create")
    public BaseResponse<LeaseOrderDto> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        return BaseResponse.success(leaseOrderService.createOrderFromCart(request));
    }

    /**
     * 获取当前用户订单列表
     */
    @GetMapping("/list")
    public BaseResponse<List<LeaseOrderDto>> getOrderList(@RequestParam(required = false) String status) {
        String userId = StpUtil.getLoginIdAsString();
        return BaseResponse.success(leaseOrderService.getUserOrderList(userId, status));
    }

    /**
     * 分页查询当前用户订单
     */
    @GetMapping("/page")
    public BaseResponse<IPage<LeaseOrderDto>> getOrderPage(@RequestParam int current,
                                                           @RequestParam int size,
                                                           @RequestParam(required = false) String status) {
        String userId = StpUtil.getLoginIdAsString();
        Page<LeaseOrderDto> page = new Page<>(current, size);
        return BaseResponse.success(leaseOrderService.getUserOrderPage(page, userId, status));
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{orderId}")
    public BaseResponse<LeaseOrderDto> getOrderDetail(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.getOrderDetail(orderId));
    }

    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    public BaseResponse<Boolean> payOrder(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.payOrder(orderId));
    }

    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public BaseResponse<Boolean> cancelOrder(@PathVariable String orderId,
                                           @RequestParam String cancelReason) {
        return BaseResponse.success(leaseOrderService.cancelOrder(orderId, cancelReason));
    }

    /**
     * 发货（管理员操作）
     */
    @PostMapping("/ship/{orderId}")
    public BaseResponse<Boolean> shipOrder(@PathVariable String orderId,
                                         @RequestParam String logisticsCompany,
                                         @RequestParam String trackingNumber) {
        return BaseResponse.success(leaseOrderService.shipOrder(orderId, logisticsCompany, trackingNumber));
    }

    /**
     * 确认收货
     */
    @PostMapping("/receive/{orderId}")
    public BaseResponse<Boolean> confirmReceive(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.confirmReceive(orderId));
    }

    /**
     * 确认归还
     */
    @PostMapping("/return/{orderId}")
    public BaseResponse<Boolean> confirmReturn(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.confirmReturn(orderId));
    }

    /**
     * 完成订单（管理员操作）
     */
    @PostMapping("/complete/{orderId}")
    public BaseResponse<Boolean> completeOrder(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.completeOrder(orderId));
    }

    /**
     * 获取用户订单状态统计
     */
    @GetMapping("/status-count")
    public BaseResponse<Map<String, Integer>> getOrderStatusCount() {
        String userId = StpUtil.getLoginIdAsString();
        return BaseResponse.success(leaseOrderService.getUserOrderStatusCount(userId));
    }
}
