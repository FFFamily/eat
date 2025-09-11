package com.tutu.api.controller.wx;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.service.RecycleOrderService;

import java.util.HashMap;
import java.util.List;

import cn.dev33.satoken.stp.StpUtil;

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

    /**
     * 获取当前登录用户作为合作方的订单列表
     * @param map 包含status参数的请求体，status可为空或"all"表示查询所有状态
     * @return 订单列表
     */
    @PostMapping("/current/list")
    public BaseResponse<List<RecycleOrder>> getPartnerOrderList(@RequestBody HashMap<String, String> map) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        // 获取状态参数
        String status = map.get("status");
        if ("ALL".equals(status)) {
            status = null;
        }
        // 查询合作方订单列表
        List<RecycleOrder> orderList = recycleOrderService.getPartnerOrderList(userId, status);
        return BaseResponse.success(orderList);
    }
}
