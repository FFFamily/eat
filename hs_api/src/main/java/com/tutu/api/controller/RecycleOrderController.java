package com.tutu.api.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.dto.RecycleOrderTraceResponse;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.request.RecycleOrderQueryRequest;
import com.tutu.recycle.request.recycle_order.CreateRecycleOrderRequest;
import com.tutu.recycle.schema.RecycleOrderInfo;
import com.tutu.recycle.service.RecycleOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recycle/order")
public class RecycleOrderController {

    @Autowired
    private RecycleOrderService recycleOrderService;

    /**
     * 添加回收订单
     * @param recycleOrder 回收订单信息
     * @return 添加结果
     */
    @PostMapping("/create")
    public BaseResponse<RecycleOrder> addRecycleOrder(@RequestBody CreateRecycleOrderRequest recycleOrder) {
        return BaseResponse.success(recycleOrderService.createOrUpdate(recycleOrder));
    }

    /**
     * 更新回收订单信息
     * @param request 回收订单信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateRecycleOrder(@RequestBody CreateRecycleOrderRequest request) {
        recycleOrderService.createOrUpdate(request);
        return BaseResponse.success();
    }

    /**
     * 订单追溯链路
     * @param orderId 订单识别码
     * @return 订单追溯链路
     */
    @GetMapping("/trace/{orderId}")
    public BaseResponse<RecycleOrderTraceResponse> getRecycleOrderTrace(@PathVariable String orderId) {
        return BaseResponse.success(recycleOrderService.getRecycleOrderTrace(orderId));
    }

    /**
     * 根据订单识别码查询对应的订单
     * @param identifyCode 订单识别码
     * @return 订单列表
     */
    @GetMapping("/identifyCode/{identifyCode}")
    public BaseResponse<List<RecycleOrder>> getRecycleOrderByIdentifyCode(@PathVariable String identifyCode) {
        List<RecycleOrder> recycleOrders = recycleOrderService.getByIdentifyCode(identifyCode);
        return BaseResponse.success(recycleOrders);
    }

    /**
     * 获取订单二维码
     * @param orderId 订单ID
     */
    @GetMapping("/qrcode/{orderId}")
    public BaseResponse<String> getOrderQrcode(@PathVariable String orderId) {
        return BaseResponse.success(recycleOrderService.createOrderQrcode(orderId));
    }

    /**
     * 给订单分配专人
     * @param orderId 订单 ID
     * @param processor 处理人 ID
     * @return 分配结果
     */
    @GetMapping("/assign")
    public BaseResponse<Void> assignProcessor(@RequestParam String orderId, @RequestParam String processor) {
        recycleOrderService.assignProcessor(orderId, processor);
        return BaseResponse.success();
    }

    /**
     * 获取专人负责的订单
     * @param recycleOrder 订单实体
     * @return 订单列表
     */
    @PostMapping("/processor/list")
    public BaseResponse<List<RecycleOrder>> getOrdersByProcessor(@RequestBody RecycleOrder recycleOrder) {
        return BaseResponse.success(recycleOrderService.getOrdersByProcessor(recycleOrder));
    }

    /**
     * 根据 ID 删除回收订单
     * @param id 回收订单 ID
     * @return 删除结果
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Void> deleteRecycleOrder(@PathVariable String id) {
        recycleOrderService.removeById(id);
        return BaseResponse.success();
    }



    /**
     * 根据 ID 查询回收订单
     * @param id 回收订单 ID
     * @return 回收订单信息
     */
    @GetMapping("/get/{id}")
    public BaseResponse<RecycleOrderInfo> getRecycleOrder(@PathVariable String id) {
        RecycleOrderInfo recycleOrderInfo = recycleOrderService.getOrderInfo(id);
        return BaseResponse.success(recycleOrderInfo);
    }
    
    /**
     * 根据父订单ID查询回收订单（包含订单明细）
     * 同一个parentId下只会有一个订单，若查询到多个则报错
     * @param parentId 父订单ID
     * @return 回收订单信息（包含订单明细和追溯信息）
     */
    @GetMapping("/parent/{parentId}")
    public BaseResponse<RecycleOrderInfo> getRecycleOrderByParentId(@PathVariable String parentId) {
        RecycleOrderInfo recycleOrderInfo = recycleOrderService.getByParentId(parentId);
        if (recycleOrderInfo == null) {
            return BaseResponse.error("未找到对应的回收订单");
        }
        return BaseResponse.success(recycleOrderInfo);
    }

    /**
     * 查询所有回收订单
     * @return 回收订单列表
     */
    @GetMapping("/all")
    public BaseResponse<List<RecycleOrder>> getAllRecycleOrders() {
        return BaseResponse.success(recycleOrderService.list());
    }

    /**
     * 分页查询回收订单
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    @PostMapping("/page")
    public BaseResponse<IPage<RecycleOrder>> getRecycleOrdersByPage(@RequestBody RecycleOrderQueryRequest queryRequest) {
        Page<RecycleOrder> ipage = new Page<>(queryRequest.getPage(), queryRequest.getSize());
        Page<RecycleOrder> result = recycleOrderService.getRecycleOrdersByPage(ipage, queryRequest);
        return BaseResponse.success(result);
    }

    /**
     * 订单结算
     * @param orderId 订单ID
     * @param settlementPdfUrl 结算单PDF URL地址
     * @return 结算结果
     */
    @PostMapping("/settlement")
    public BaseResponse<Void> settlementOrder(@RequestParam String orderId, @RequestParam String settlementPdfUrl) {
        recycleOrderService.settlementOrder(orderId, settlementPdfUrl);
        return BaseResponse.success();
    }

    /**
     * 订单申请
     * @param orderId 订单ID
     * @param applicationPdfUrl 申请单PDF URL地址
     * @return 申请结果
     */
    @PostMapping("/application")
    public BaseResponse<Void> applicationOrder(@RequestParam String orderId, @RequestParam String applicationPdfUrl) {
        recycleOrderService.applicationOrder(orderId, applicationPdfUrl);
        return BaseResponse.success();
    }

    /**
     * 分页查询仓储入库订单
     * @param queryRequest 查询请求参数
     * @return 分页结果
     */
    @PostMapping("/storage/inbound/page")
    public BaseResponse<IPage<RecycleOrder>> getStorageInboundOrdersByPage(@RequestBody RecycleOrderQueryRequest queryRequest) {
        Page<RecycleOrder> ipage = new Page<>(queryRequest.getPage(), queryRequest.getSize());
        Page<RecycleOrder> result = recycleOrderService.getStorageInboundOrdersByPage(ipage, queryRequest);
        return BaseResponse.success(result);
    }
}
