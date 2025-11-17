package com.tutu.api.controller.wx;

import cn.hutool.core.bean.BeanUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.TransportOrderDTO;
import com.tutu.recycle.dto.UserOrderInfo;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.enums.TransportStatusEnum;
import com.tutu.recycle.response.WxTransportOrderListResponse;
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
@RequestMapping("/wx/employee/transport")
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
    @GetMapping("/available")
    public BaseResponse<List<WxTransportOrderListResponse>> getAvailableOrders() {
        List<WxTransportOrderListResponse> orders = userOrderService.getAvailableTransportOrders();
        return BaseResponse.success(orders);
    }

    /**
     * 抢单操作
     * @param orderId 订单ID
     * @param processorId 经办人ID
     * @param processorName 经办人姓名
     * @param processorPhone 经办人电话
     * @return 抢单结果
     */
    @PostMapping("/grab")
    public BaseResponse<Boolean> grabOrder(
            @RequestParam String orderId,
            @RequestParam String processorId,
            @RequestParam String processorName,
            @RequestParam String processorPhone) {
        try {
            boolean result = recycleOrderService.grabOrder(orderId, processorId, processorName, processorPhone);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 交付大厅列表
     * @return 交付大厅的运输订单列表
     */
    @GetMapping("/delivery-hall")
    public BaseResponse<List<TransportOrderDTO>> getDeliveryHallOrders() {
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(TransportStatusEnum.GRABBED.getCode());
        List<TransportOrderDTO> result = convertToDTO(orders);
        return BaseResponse.success(result);
    }

    /**
     * 去交付操作（从交付大厅到运输中）
     * @param orderId 订单ID
     * @param deliveryAddress 送达地址（可选）
     * @return 交付结果
     */
    @PostMapping("/deliver")
    public BaseResponse<Boolean> deliverOrder(
            @RequestParam String orderId,
            @RequestParam(required = false) String deliveryAddress) {
        try {
            boolean result = recycleOrderService.deliverOrder(orderId, deliveryAddress);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 运输中列表
     * @return 运输中的订单列表
     */
    @GetMapping("/transporting")
    public BaseResponse<List<TransportOrderDTO>> getTransportingOrders() {
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(TransportStatusEnum.TRANSPORTING.getCode());
        List<TransportOrderDTO> result = convertToDTO(orders);
        return BaseResponse.success(result);
    }

    /**
     * 批量确认送达
     * @param orderIds 订单ID列表
     * @return 确认结果
     */
    @PostMapping("/batch-confirm-arrival")
    public BaseResponse<Boolean> batchConfirmArrival(@RequestBody List<String> orderIds) {
        try {
            boolean result = recycleOrderService.batchConfirmArrival(orderIds);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 已送达列表
     * @return 已送达的订单列表
     */
    @GetMapping("/arrived")
    public BaseResponse<List<TransportOrderDTO>> getArrivedOrders() {
        List<RecycleOrder> orders = recycleOrderService.getTransportOrdersByStatus(TransportStatusEnum.ARRIVED.getCode());
        List<TransportOrderDTO> result = convertToDTO(orders);
        return BaseResponse.success(result);
    }

    /**
     * 运输单详情
     * @param orderId 订单ID
     * @return 运输单详情
     */
    @GetMapping("/detail/{orderId}")
    public BaseResponse<TransportOrderDTO> getTransportOrderDetail(@PathVariable String orderId) {
        try {
            RecycleOrder order = recycleOrderService.getById(orderId);
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
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * 查看交付单（已送达页面-查看-交付单）
     * @param orderId 订单ID
     * @return 交付单PDF URL
     */
    @GetMapping("/delivery-note/{orderId}")
    public BaseResponse<String> getDeliveryNotePdfUrl(@PathVariable String orderId) {
        try {
            String pdfUrl = recycleOrderService.getDeliveryNotePdfUrl(orderId);
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
     * @param orderId 订单ID
     * @param pdfUrl 交付单PDF URL
     * @return 保存结果
     */
    @PostMapping("/delivery-note/save")
    public BaseResponse<Boolean> saveDeliveryNotePdf(
            @RequestParam String orderId,
            @RequestParam String pdfUrl) {
        try {
            boolean result = recycleOrderService.saveDeliveryNotePdf(orderId, pdfUrl);
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    // ==================== 分拣中心接口 ====================

    /**
     * 分拣中心-交付大厅列表（送货上门的加工订单）
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/sorting/delivery-hall")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder>> getSortingDeliveryHall(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> result =
            recycleOrderService.getDeliveryHallList(page);
        return BaseResponse.success(result);
    }

    /**
     * 分拣中心-开始分拣列表（待分拣的加工订单）
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/sorting/start")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder>> getStartSortingList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> result =
            recycleOrderService.getStartSortingList(page);
        return BaseResponse.success(result);
    }

    /**
     * 分拣中心-结果暂存列表（分拣中的加工订单）
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/sorting/temp")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder>> getSortingTempList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> result =
            recycleOrderService.getSortingTempList(page);
        return BaseResponse.success(result);
    }

    /**
     * 分拣中心-已分拣列表（已分拣的加工订单）
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    @GetMapping("/sorting/sorted")
    public BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder>> getSortedList(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> page =
            new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(current, size);
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<RecycleOrder> result =
            recycleOrderService.getSortedList(page);
        return BaseResponse.success(result);
    }

    /**
     * 根据识别码获取订单
     * @param identifyCode 识别码
     * @return 订单信息
     */
    @GetMapping("/sorting/by-code/{identifyCode}")
    public BaseResponse<RecycleOrder> getOrderByIdentifyCode(@PathVariable String identifyCode) {
        try {
            List<RecycleOrder> orders = recycleOrderService.getByIdentifyCode(identifyCode);
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

