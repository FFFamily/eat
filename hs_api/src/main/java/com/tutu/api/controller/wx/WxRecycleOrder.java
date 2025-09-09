package com.tutu.api.controller.wx;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.service.RecycleOrderService;

@RestController
@RequestMapping("/wx/recycle/order")    
public class WxRecycleOrder {
    @Resource
    private RecycleOrderService recycleOrderService;

    // 创建订单
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody RecycleOrder order) {
        recycleOrderService.createWxOrder(order);
        return BaseResponse.success();
    }
}
