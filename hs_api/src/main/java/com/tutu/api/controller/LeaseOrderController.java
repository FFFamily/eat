package com.tutu.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.json.JSONUtil;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.dto.CreateOrderRequest;
import com.tutu.lease.dto.CreateOrderFromGoodsRequest;
import com.tutu.lease.dto.LeaseOrderDto;
import com.tutu.lease.entity.LeaseOrder;
import com.tutu.lease.enums.LeaseOrderStatusEnum;
import com.tutu.lease.service.LeaseOrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
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

    // 获取订单所有的状态枚举
    @GetMapping("/status/all")
    public BaseResponse<List<Map<String, String>>> getAllStatus() {
        LeaseOrderStatusEnum[] values = LeaseOrderStatusEnum.values();
        List<Map<String, String>> list = new ArrayList<>();
        for (LeaseOrderStatusEnum value : values) {
            Map<String, String> map = new HashMap<>();
            map.put("value", value.getCode());
            map.put("label", value.getDescription());
            list.add(map);
        }
        return BaseResponse.success(list);
    }
    /**
     * 通过商品信息直接创建订单
     */
    @PostMapping("/create/fromGood")
    public BaseResponse<Void> createOrderFromGoods(@RequestBody @Valid CreateOrderFromGoodsRequest request) {
        leaseOrderService.createOrderFromGoods(request);
        return BaseResponse.success();
    }
    /**
     * 分页查询当前用户订单
     */
    @GetMapping("/my/page")
    public BaseResponse<IPage<LeaseOrderDto>> getMyOrderPage(@RequestParam int pageNum,
                                                           @RequestParam int pageSize,
                                                           @RequestParam(required = false) String status) {
        String userId = StpUtil.getLoginIdAsString();
        Page<LeaseOrderDto> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(leaseOrderService.getUserOrderPage(page, userId, status));
    }
    /**
     * 分页查询订单
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @param order 订单对象
     * @return 订单分页对象
     */
    @GetMapping("/page")
    public BaseResponse<IPage<LeaseOrder>> getOrderPage(@RequestParam int pageNum,@RequestParam int pageSize,LeaseOrder order) {
        Page<LeaseOrder> page = new Page<>(pageNum, pageSize);
        IPage<LeaseOrder> result = leaseOrderService.getOrderPage(page,order);;
        return BaseResponse.success(result);
    }
    /**
     * 编辑订单
     * @param orderId 订单ID
     * @param order 订单对象
     * @return 是否成功
     */
    @PostMapping("/update/{orderId}")
    public BaseResponse<Boolean> editOrder(@PathVariable String orderId,@RequestBody HashMap<String, Object> orderMap) {
        LeaseOrder order = JSONUtil.parseObj(orderMap).toBean(LeaseOrder.class);;
        leaseOrderService.editOrder(orderId, order);
        return BaseResponse.success();
    }

    /**
     * 审核订单
     */
    @PostMapping("/approve/{orderId}")
    public BaseResponse<Boolean> reviewOrder(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.reviewOrder(orderId, true));
    }

    /**
     * 完成租赁
     */
    @PostMapping("/finish-leasing/{orderId}")
    public BaseResponse<Boolean> finishLeasing(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.finishLeasing(orderId));
    }

    /**
     * 完成开票
     */
    @PostMapping("/complete-invoice/{orderId}")
    public BaseResponse<Boolean> completeInvoice(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.completeInvoice(orderId));
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
     * 获取订单详情
     */
    @GetMapping("/detail/{orderId}")
    public BaseResponse<LeaseOrderDto> getOrderDetail(@PathVariable String orderId) {
        return BaseResponse.success(leaseOrderService.getOrderDetail(orderId));
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
