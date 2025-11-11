package com.tutu.api.controller.wx;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.UserOrderInfo;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.request.QueryOrderByIdRequest;
import com.tutu.recycle.service.UserOrderService;

import cn.dev33.satoken.stp.StpUtil;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/wx/user/order")
public class WxUserOrderController {

    @Autowired
    private UserOrderService userOrderService;

    // 创建订单
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody UserOrder order) {
        userOrderService.createUserOrder(order);
        return BaseResponse.success();
    }
     /**
     * 查询用户订单信息及其子回收订单
     * 根据回收订单类型，将订单信息映射到不同的字段
     * @param id 用户订单ID
     * @return 用户订单信息（包含子回收订单）
     */
    @PostMapping("/getById")
    public BaseResponse<UserOrderInfo> getUserOrderInfo(@RequestBody QueryOrderByIdRequest request) {
        UserOrderInfo userOrderInfo = userOrderService.getUserOrderInfo(request.getId());
        return BaseResponse.success(userOrderInfo);
    }

       /**
     * 获取当前登录用户作为合作方的订单列表
     * @param map 包含status参数的请求体，status可为空或"all"表示查询所有状态
     * @return 订单列表
     */
    @PostMapping("/current/list")
    public BaseResponse<List<UserOrder>> getPartnerOrderList(@RequestBody UserOrder order) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        order.setContractPartner(userId);
        // 查询合作方订单列表
        List<UserOrder> orderList = userOrderService.getUserOrderList(order);
        return BaseResponse.success(orderList);
    }
}
