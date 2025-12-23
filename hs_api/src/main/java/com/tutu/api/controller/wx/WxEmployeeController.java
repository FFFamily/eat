package com.tutu.api.controller.wx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.SortingOrderDTO;
import com.tutu.recycle.dto.TransportOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.enums.TransportStatusEnum;
import com.tutu.recycle.request.*;
import com.tutu.recycle.response.WxTransportOrderListResponse;
import com.tutu.recycle.response.SortingDeliveryHallResponse;
import com.tutu.recycle.service.RecycleOrderService;
import com.tutu.recycle.service.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 微信员工端控制器 - 运输中心
 */
@RestController
@RequestMapping("/wx/employee")
public class WxEmployeeController {

    @Autowired
    private RecycleOrderService recycleOrderService;

    @Autowired
    private UserOrderService userOrderService;

    /**
     * 可接单列表（抢单大厅）
     * 查询当前阶段为运输，且不存在运输子订单的用户订单
     * @return 可接单的用户订单列表
     */
    @PostMapping("/transport/available")
    public BaseResponse<List<WxTransportOrderListResponse>> getAvailableOrders() {
        List<WxTransportOrderListResponse> orders = userOrderService.getAvailableTransportOrders();
        return BaseResponse.success(orders);
    }

    /**
     * 抢单操作
     * @param request 抢单请求（包含主订单ID和经办人ID）
     * @return 抢单结果
     */
    @PostMapping("/transport/grab")
    public BaseResponse<Boolean> grabOrder(@RequestBody GrabOrderRequest request) {
        try {
            // 调用服务层抢单方法，经办人ID由前端传递
            boolean result = recycleOrderService.grabOrder(request.getOrderId(), request.getProcessorId());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 交付大厅列表
     * @param request 运输订单查询请求（包含经办人ID）
     * @return 交付大厅的运输订单列表
     */
    @PostMapping("/transport/delivery-hall")
    public BaseResponse<List<TransportOrderDTO>> getDeliveryHallOrders(@RequestBody TransportOrderListRequest request) {
        if (request == null || StrUtil.isBlank(request.getProcessorId())) {
            return BaseResponse.error("经办人ID不能为空");
        }
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(TransportStatusEnum.GRABBED.getCode(), request.getProcessorId());
        List<TransportOrderDTO> result = convertToDTO(orders);
        return BaseResponse.success(result);
    }

    /**
     * 去交付操作（从交付大厅到运输中）
     * @param request 交付请求
     * @return 交付结果
     */
    @PostMapping("/transport/deliver")
    public BaseResponse<Boolean> deliverOrder(@RequestBody DeliverOrderRequest request) {
        try {
            boolean result = recycleOrderService.deliverOrder(request);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 运输中列表
     * @return 运输中的订单列表
     */
    @PostMapping("/transport/transporting")
    public BaseResponse<List<TransportOrderDTO>> getTransportingOrders() {
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(TransportStatusEnum.TRANSPORTING.getCode(), null);
        List<TransportOrderDTO> result = convertToDTO(orders);
        return BaseResponse.success(result);
    }

    /**
     * 确认送达
     * @param request 订单ID请求
     * @return 确认结果
     */
    @PostMapping("/transport/confirm-arrival")
    public BaseResponse<Boolean> confirmArrival(@RequestBody QueryOrderByIdRequest request) {
        try {
            boolean result = recycleOrderService.confirmArrival(request.getOrderId());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 已送达列表
     * @return 已送达的订单列表
     */
    @PostMapping("/transport/arrived")
    public BaseResponse<List<TransportOrderDTO>> getArrivedOrders(@RequestBody TransportOrderListRequest request) {
        if (request == null || StrUtil.isBlank(request.getProcessorId())) {
            return BaseResponse.error("经办人ID不能为空");
        }
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(
            TransportStatusEnum.ARRIVED.getCode(),
            request.getProcessorId());
        List<TransportOrderDTO> result = convertToDTO(orders);
        return BaseResponse.success(result);
    }

    /**
     * 运输单详情
     * @param request 订单ID请求
     * @return 运输单详情
     */
    @PostMapping("/transport/detail")
    public BaseResponse<TransportOrderDTO> getTransportOrderDetail(@RequestBody QueryOrderByIdRequest request) {
        try {
            RecycleOrder order = recycleOrderService.getById(request.getOrderId());
            if (order == null) {
                return BaseResponse.error("订单不存在");
            }

            TransportOrderDTO dto = new TransportOrderDTO();
            BeanUtil.copyProperties(order, dto);

            // 获取父订单的交付时间
            if (order.getParentId() != null) {
                UserOrder userOrder = userOrderService.getById(order.getParentId());
                if (userOrder != null) {
                    dto.setDeliveryTime(userOrder.getDeliveryTime());
                }
            }

            return BaseResponse.success(dto);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }



    /**
     * 转换为 DTO 列表
     * @param orders 回收订单列表
     * @return DTO 列表
     */
    private List<TransportOrderDTO> convertToDTO(List<RecycleOrder> orders) {
        if (orders == null || orders.isEmpty()) {
            return new ArrayList<>();
        }

        // 获取所有父订单ID
        List<String> parentIds = orders.stream()
                .map(RecycleOrder::getParentId)
                .filter(parentId -> parentId != null)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询父订单
        Map<String, UserOrder> userOrderMap = parentIds.isEmpty()
            ? Map.of()
            : userOrderService.listByIds(parentIds).stream()
                .collect(Collectors.toMap(UserOrder::getId, Function.identity()));

        // 转换为DTO
        return orders.stream().map(order -> {
            TransportOrderDTO dto = new TransportOrderDTO();
            BeanUtil.copyProperties(order, dto);

            // 设置父订单的交付时间
            if (order.getParentId() != null) {
                UserOrder userOrder = userOrderMap.get(order.getParentId());
                if (userOrder != null) {
                    dto.setDeliveryTime(userOrder.getDeliveryTime());
                    dto.setMainOrderNo(userOrder.getNo());
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 查看交付单（已送达页面-查看-交付单）
     * @param request 订单ID请求
     * @return 交付单PDF URL
     */
    @PostMapping("/transport/delivery-note")
    public BaseResponse<String> getDeliveryNotePdfUrl(@RequestBody QueryOrderByIdRequest request) {
        try {
            String pdfUrl = recycleOrderService.getDeliveryNotePdfUrl(request.getOrderId());
            if (pdfUrl == null || pdfUrl.isEmpty()) {
                return BaseResponse.error("该订单暂无交付单");
            }
            return BaseResponse.success(pdfUrl);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 保存交付单PDF URL（签署后保存）
     * @param request 保存交付单PDF请求
     * @return 保存结果
     */
    @PostMapping("/transport/delivery-note/save")
    public BaseResponse<Boolean> saveDeliveryNotePdf(@RequestBody SaveDeliveryNotePdfRequest request) {
        try {
            boolean result = recycleOrderService.saveDeliveryNotePdf(request.getOrderId(), request.getPdfUrl());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 校验订单识别码是否属于指定订单
     * @param request 校验请求
     * @return true/false
     */
    @PostMapping("/transport/validate-identify-code")
    public BaseResponse<Boolean> validateIdentifyCode(@RequestBody ValidateOrderIdentifyCodeRequest request) {
        try {
            boolean result = recycleOrderService.validateOrderIdentifyCode(request.getOrderId(), request.getIdentifyCode());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }


}

