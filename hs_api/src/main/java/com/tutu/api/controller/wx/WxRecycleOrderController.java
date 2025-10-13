package com.tutu.api.controller.wx;

import jakarta.annotation.Resource;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.request.QueryOrderByIdRequest;
import com.tutu.recycle.request.QueryOrderByIdentifyCodeRequest;
import com.tutu.recycle.request.TransportOrderSubmitRequest;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.request.ProcessingOrderSubmitRequest;
import com.tutu.recycle.service.RecycleOrderService;

import java.util.List;

import cn.dev33.satoken.stp.StpUtil;

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

    /**
     * 获取当前登录用户作为合作方的订单列表
     * @param map 包含status参数的请求体，status可为空或"all"表示查询所有状态
     * @return 订单列表
     */
    @PostMapping("/current/list")
    public BaseResponse<List<RecycleOrder>> getPartnerOrderList(@RequestBody RecycleOrder order) {
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        order.setContractPartner(userId);
        // 查询合作方订单列表
        List<RecycleOrder> orderList = recycleOrderService.getOrderList(order);
        return BaseResponse.success(orderList);
    }

    /**
     * 根据订单识别号查询订单
     * @param request 查询请求
     * @return 订单信息
     */
    @PostMapping("/queryByIdentifyCode")
    public BaseResponse<RecycleOrder> queryByIdentifyCode(@RequestBody QueryOrderByIdentifyCodeRequest request) {
        if (request.getIdentifyCode() == null || request.getIdentifyCode().trim().isEmpty()) {
            return BaseResponse.error("订单识别号不能为空");
        }
        
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 根据订单识别号查询订单（带权限控制）
        RecycleOrder order = recycleOrderService.getOrderByIdentifyCode(request.getIdentifyCode(), userId);
        return BaseResponse.success(order);
    }

    /**
     * 根据订单ID获取订单详情
     * @param request 查询请求
     * @return 订单详情信息
     */
    @PostMapping("/getById")
    public BaseResponse<RecycleOrder> getOrderById(@RequestBody QueryOrderByIdRequest request) {
        if (request.getId() == null || request.getId().trim().isEmpty()) {
            return BaseResponse.error("订单ID不能为空");
        }
        
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 根据订单ID获取订单详情
        RecycleOrder order = recycleOrderService.getOrderByIdWithPermission(request.getId(), userId);
        return BaseResponse.success(order);
    }

    /**
     * 运输订单提交
     * @param request 运输订单提交请求
     * @return 提交结果
     */
    @PostMapping("/submitTransport")
    public BaseResponse<String> submitTransportOrder(@RequestBody TransportOrderSubmitRequest request) {
        if (request.getOrderId() == null || request.getOrderId().trim().isEmpty()) {
            return BaseResponse.error("订单ID不能为空");
        }
        
        if (request.getOrderNodeImg() == null || request.getOrderNodeImg().trim().isEmpty()) {
            return BaseResponse.error("订单图片不能为空");
        }
        
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty()) {
            return BaseResponse.error("交付地址不能为空");
        }
        
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 提交运输订单
        recycleOrderService.submitTransportOrder(request.getOrderId(), request.getOrderNodeImg(), request.getDeliveryAddress(), userId);
        return BaseResponse.success("运输订单提交成功");
    }

    /**
     * 加工订单提交
     * @param request 加工订单提交请求
     * @return 提交结果
     */
    @PostMapping("/submitProcessing")
    public BaseResponse<String> submitProcessingOrder(@RequestBody ProcessingOrderSubmitRequest request) {
        if (request.getOrderId() == null || request.getOrderId().trim().isEmpty()) {
            return BaseResponse.error("订单ID不能为空");
        }
        
        if (request.getOrderNodeImg() == null || request.getOrderNodeImg().trim().isEmpty()) {
            return BaseResponse.error("订单图片不能为空");
        }
        
        // 获取当前登录用户ID
        String userId = StpUtil.getLoginIdAsString();
        
        // 提交加工订单
        recycleOrderService.submitProcessingOrder(request.getOrderId(), request.getOrderNodeImg(), request.getItems(), userId);
        return BaseResponse.success("加工订单提交成功");
    }
}
