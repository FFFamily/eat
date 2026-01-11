package com.tutu.api.controller.wx;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.SortingOrderDTO;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.request.*;
import com.tutu.recycle.response.SortingDeliveryHallResponse;
import com.tutu.recycle.service.RecycleOrderService;
import com.tutu.recycle.service.UserOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wx/employee")
public class WxEmployeeSortingController {
    @Autowired
    private RecycleOrderService recycleOrderService;

    @Autowired
    private UserOrderService userOrderService;

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
     * 我的交付大厅
     * @param request 查询条件（包含经办人ID）
     * @return 可分拣的订单列表
     */
    @PostMapping("/sorting/delivery-center")
    public BaseResponse<List<SortingDeliveryHallResponse>> getSortingDeliveryCenter(
            @RequestBody SortingOrderPageRequest request) {
        List<SortingDeliveryHallResponse> orders = null;
        if (request != null) {
            if (request.getProcessorId() == null){
                return BaseResponse.error("未能检测到经办人");
            }
            orders = userOrderService.getSortingHomeDeliveryHallOrders(request.getProcessorId());
        }
        return BaseResponse.success(orders);
    }

    /**
     * 分拣单详情
     * @param request 订单ID请求
     * @return 分拣单详情
     */
    @PostMapping("/sorting/detail")
    public BaseResponse<SortingOrderDTO> getSortingOrderDetail(@RequestBody QueryOrderByIdRequest request) {
        try {
            RecycleOrder order = recycleOrderService.getById(request.getOrderId());
            if (order == null) {
                return BaseResponse.error("订单不存在");
            }

            SortingOrderDTO dto = new SortingOrderDTO();
            BeanUtil.copyProperties(order, dto);

            // 获取父订单的交付时间
            if (order.getParentId() != null) {
                UserOrder userOrder = userOrderService.getById(order.getParentId());
                if (userOrder != null) {
                    dto.setDeliveryTime(userOrder.getDeliveryTime());
                    dto.setParentCode(userOrder.getNo());
                }
            }

            return BaseResponse.success(dto);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    /**
     * 我的分拣列表（指定经办人的未分拣订单）
     * @param request 查询条件（包含经办人ID）
     * @return 未分拣订单列表
     */
    @PostMapping("/sorting/my-pending")
    public BaseResponse<List<RecycleOrder>> getMyPendingSortingList(
            @RequestBody SortingOrderPageRequest request) {
        try {
            if (request == null || StrUtil.isBlank(request.getProcessorId())) {
                return BaseResponse.error("经办人ID不能为空");
            }
            List<RecycleOrder> result = recycleOrderService.getMyPendingSortingList(request.getProcessorId());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }

    /**
     * 分拣中心-已分拣列表（已分拣的加工订单）
     * @param request 查询条件
     * @return 已分拣列表
     */
    @PostMapping("/sorting/sorted")
    public BaseResponse<List<RecycleOrder>> getSortedList(
            @RequestBody SortingOrderPageRequest request) {
        if (request == null || StrUtil.isBlank(request.getProcessorId())) {
            return BaseResponse.error("经办人ID不能为空");
        }
        List<RecycleOrder> result = recycleOrderService.getSortedList(request.getProcessorId());
        return BaseResponse.success(result);
    }
    /**
     * 判断能否分拣
     * 检查主订单的交付状态是否为已交付
     * @param request 订单ID请求
     * @return 判断结果，true表示可以分拣
     */
    @PostMapping("/sorting/can-sort")
    public BaseResponse<Boolean> canSort(@RequestBody QueryOrderByIdRequest request) {
        try {
            if (request == null || StrUtil.isBlank(request.getOrderId())) {
                return BaseResponse.error("订单ID不能为空");
            }
            boolean result = recycleOrderService.canSort(request.getOrderId());
            return BaseResponse.success(result);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
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
     * 提交分拣结果（完成分拣）
     * @param request 分拣请求
     * @return 提交结果
     */
    @PostMapping("/sorting/submit")
    public BaseResponse<Boolean> submitSortingResult(@RequestBody ProcessingOrderSubmitRequest request) {
        try {
            recycleOrderService.submitSortingResult(request);
            return BaseResponse.success(true);
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
}
