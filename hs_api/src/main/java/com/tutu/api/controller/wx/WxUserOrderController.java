package com.tutu.api.controller.wx;

import com.tutu.recycle.request.WxUserCreateOrderRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.UserOrderInfo;
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

    // 检查是否具备创建订单的能力
    @PostMapping("/check")
    public BaseResponse<Boolean> checkPermission(@RequestBody WxUserCreateOrderRequest order) {
        String userId = StpUtil.getLoginIdAsString();
        // 检查用户是否具备创建订单的权限
        Boolean flag =  userOrderService.checkCreateOrderPermission(userId,order);
        return BaseResponse.success(flag);
    }
    // 创建订单
    @PostMapping("/create")
    public BaseResponse<String> create(@RequestBody WxUserCreateOrderRequest order) {
        userOrderService.createWxUserOrder(order);
        return BaseResponse.success();
    }
     /**
     * 查询用户订单信息及其子回收订单
     * 根据回收订单类型，将订单信息映射到不同的字段
     * @param orderId 用户订单ID
     * @return 用户订单信息（包含子回收订单）
     */
    @PostMapping("/getById")
    public BaseResponse<UserOrderInfo> getUserOrderInfo(@RequestBody QueryOrderByIdRequest request) {
        UserOrderInfo userOrderInfo = userOrderService.getUserOrderInfo(request.getOrderId());
        return BaseResponse.success(userOrderInfo);
    }

       /**
     * 获取当前登录用户作为合作方的订单列表
     * @param order 包含status参数的请求体，status可为空或"all"表示查询所有状态
     * @return 订单列表
     */
    @PostMapping("/current/list")
    public BaseResponse<List<UserOrder>> getPartnerOrderList(@RequestBody UserOrder order) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        order.setContractPartner(userId);
        // 查询合作方订单列表
        List<UserOrder> orderList = userOrderService.getWxUserOrderList(order);
        return BaseResponse.success(orderList);
    }

    /**
     * 确认结算结果
     * 只有待客户确认的订单才可以确认结算
     * 更新阶段、结算确认时间和结算状态为已结算
     * @param request 订单ID请求
     * @return 操作结果
     */
    @PostMapping("/confirmSettlement")
    public BaseResponse<String> confirmSettlement(@RequestBody QueryOrderByIdRequest request) {
        userOrderService.confirmSettlementResult(request.getOrderId());
        return BaseResponse.success();
    }

    /**
     * 否定结算
     * 只有待客户确认的订单才可以否定结算
     * 更新结算状态为已驳回
     * @param request 订单ID请求
     * @return 操作结果
     */
    @PostMapping("/rejectSettlement")
    public BaseResponse<String> rejectSettlement(@RequestBody QueryOrderByIdRequest request) {
        userOrderService.rejectSettlement(request);
        return BaseResponse.success();
    }
}
