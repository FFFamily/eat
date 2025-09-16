package com.tutu.api.controller.wx.lease;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.dto.CreateOrderRequest;
import com.tutu.lease.dto.LeaseOrderDto;
import com.tutu.lease.service.LeaseOrderService;

import java.util.HashMap;
import java.util.List;

import cn.dev33.satoken.stp.StpUtil;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/wx/lease/order")
public class WxLeaseOrderController {

    @Autowired
    private LeaseOrderService leaseOrderService;

     /**
     * 从购物车创建订单
     */
    @PostMapping("/create")
    public BaseResponse<Void> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        leaseOrderService.createOrderFromCart(request);
        return BaseResponse.success();
    }

    /**
     * 获取当前用户订单列表
     */
    @PostMapping("/list")
    public BaseResponse<List<LeaseOrderDto>> getOrderList(@RequestBody HashMap<String, String> map) {
        String userId = StpUtil.getLoginIdAsString();
        String status = map.get("status");
        if ("ALL".equals(status)) {
            status = null;
        }
        return BaseResponse.success(leaseOrderService.getUserOrderList(userId, status));
    }
    
}
