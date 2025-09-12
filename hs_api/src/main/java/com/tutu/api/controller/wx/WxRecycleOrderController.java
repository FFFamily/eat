package com.tutu.api.controller.wx;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.service.RecycleOrderService;

import cn.dev33.satoken.stp.StpUtil;

import java.util.List;

@RestController
@RequestMapping("/wx/recycle/order")    
public class WxRecycleOrderController {
    @Resource
    private RecycleOrderService recycleOrderService;

    // 创建订单
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody RecycleOrder order) {
        recycleOrderService.createWxOrder(order);
        return BaseResponse.success();
    }

    // 获取当前登录人对应状态的所有订单
    @GetMapping("/current/list")
    public BaseResponse<List<RecycleOrder>> getCurrentUserOrders(@RequestParam(required = false) String status) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        // 根据合作方和状态查询订单
        List<RecycleOrder> orders = recycleOrderService.getPartnerOrderList(userId, status);
        return BaseResponse.success(orders);
    }
}
