package com.tutu.api.controller.wx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.common.Response.BaseResponse;
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
     * @return 交付大厅的运输订单列表
     */
    @PostMapping("/transport/delivery-hall")
    public BaseResponse<List<TransportOrderDTO>> getDeliveryHallOrders() {
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(TransportStatusEnum.GRABBED.getCode(), null);
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
     * 分拣单详情
     * @param request 订单ID请求
     * @return 分拣单详情
     */
    @PostMapping("/sorting/detail")
    public BaseResponse<UserOrder> getSortingOrderDetail(@RequestBody QueryOrderByIdRequest request) {
        try {
            if (request == null || StrUtil.isBlank(request.getOrderId())) {
                return BaseResponse.error("主订单ID不能为空");
            }
            UserOrder userOrder = userOrderService.getById(request.getOrderId());
            if (userOrder == null) {
                return BaseResponse.error("主订单不存在");
            }
//            RecycleOrder order = recycleOrderService.getProcessingOrderByParentId(request.getOrderId());
//            if (order == null) {
//                return BaseResponse.error("该订单暂无分拣信息");
//            }
//            SortingOrderDTO dto = new SortingOrderDTO();
//            BeanUtil.copyProperties(order, dto);

//            dto.setParentId(userOrder.getId());
//            dto.setDeliveryTime(userOrder.getDeliveryTime());
//            if (StrUtil.isBlank(dto.getContractPartnerName())) {
//                dto.setContractPartnerName(userOrder.getContractPartnerName());
//            }

            return BaseResponse.success(userOrder);
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

    // ==================== 分拣中心接口 ====================

    /**
     * 分拣中心
     * 条件：用户订单处于加工阶段，且暂无加工子订单
     * @return 可分拣的订单列表
     */
    @PostMapping("/sorting/delivery-hall")
    public BaseResponse<List<SortingDeliveryHallResponse>> getSortingDeliveryHall() {
        List<SortingDeliveryHallResponse> orders = userOrderService.getSortingDeliveryHallOrders();
        return BaseResponse.success(orders);
    }

    /**
     * 我的交付大厅
     * @param request 查询条件（包含经办人ID）
     * @return 可分拣的订单列表
     */
    @PostMapping("/sorting/delivery-center")
    public BaseResponse<List<SortingDeliveryHallResponse>> getSortingDeliveryCenter(
            @RequestBody SortingOrderPageRequest request) {
        String processorId = request != null ? request.getProcessorId() : null;
        List<SortingDeliveryHallResponse> orders = userOrderService.getSortingHomeDeliveryHallOrders(processorId);
        return BaseResponse.success(orders);
    }

    /**
     * 分拣中心-抢单
     * @param request 抢单请求
     * @return 抢单结果
     */
    @PostMapping("/sorting/grab")
    public BaseResponse<Boolean> grabSortingOrder(@RequestBody GrabOrderRequest request) {
        try {
            boolean result = recycleOrderService.grabSortingOrder(request.getOrderId(), request.getProcessorId());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    

    /**
     * 分拣中心-开始分拣列表（待分拣的加工订单）
     * @param request 查询条件
     * @return 待分拣列表
     */
    @PostMapping("/sorting/start")
    public BaseResponse<List<RecycleOrder>> getStartSortingList(
            @RequestBody SortingOrderPageRequest request) {
        String processorId = request != null ? request.getProcessorId() : null;
        List<RecycleOrder> result = recycleOrderService.getStartSortingList(processorId);
        return BaseResponse.success(result);
    }

    /**
     * 分拣中心-结果暂存列表（分拣中的加工订单）
     * @param request 查询条件
     * @return 分拣中列表
     */
    @PostMapping("/sorting/temp")
    public BaseResponse<List<RecycleOrder>> getSortingTempList(
            @RequestBody SortingOrderPageRequest request) {
        String processorId = request != null ? request.getProcessorId() : null;
        List<RecycleOrder> result = recycleOrderService.getSortingTempList(processorId);
        return BaseResponse.success(result);
    }

    /**
     * 分拣中心-已分拣列表（已分拣的加工订单）
     * @param request 查询条件
     * @return 已分拣列表
     */
    @PostMapping("/sorting/sorted")
    public BaseResponse<List<RecycleOrder>> getSortedList(
            @RequestBody SortingOrderPageRequest request) {
        String processorId = request != null ? request.getProcessorId() : null;
        List<RecycleOrder> result = recycleOrderService.getSortedList(processorId);
        return BaseResponse.success(result);
    }

    /**
     * 根据识别码获取订单
     * @param request 识别码请求
     * @return 订单信息
     */
    @PostMapping("/sorting/by-code")
    public BaseResponse<RecycleOrder> getOrderByIdentifyCode(@RequestBody QueryOrderByIdentifyCodeRequest request) {
        try {
            List<RecycleOrder> orders = recycleOrderService.getByIdentifyCode(request.getIdentifyCode());
            if (orders.isEmpty()) {
                return BaseResponse.error("未找到该识别码对应的订单");
            }
            return BaseResponse.success(orders.getFirst());
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 保存分拣结果（暂存）
     * @param request 分拣请求
     * @return 保存结果
     */
    @PostMapping("/sorting/save")
    public BaseResponse<Boolean> saveSortingResult(@RequestBody com.tutu.recycle.request.ProcessingOrderSubmitRequest request) {
        try {
            boolean result = recycleOrderService.saveSortingResult(request);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 提交分拣结果（完成分拣）
     * @param request 分拣请求
     * @return 提交结果
     */
    @PostMapping("/sorting/submit")
    public BaseResponse<Boolean> submitSortingResult(@RequestBody com.tutu.recycle.request.ProcessingOrderSubmitRequest request) {
        try {
            boolean result = recycleOrderService.submitSortingResult(request);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}

