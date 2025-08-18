package com.tutu.api.controller;

import cn.hutool.core.util.PageUtil;
import jakarta.servlet.http.HttpServletResponse;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleOrder;
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
    public BaseResponse<RecycleOrder> addRecycleOrder(@RequestBody RecycleOrder recycleOrder) {
        return BaseResponse.success(recycleOrderService.createOrder(recycleOrder));
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
    @DeleteMapping("/{id}")
    public BaseResponse<Void> deleteRecycleOrder(@PathVariable String id) {
        recycleOrderService.removeById(id);
        return BaseResponse.success();
    }

    /**
     * 更新回收订单信息
     * @param recycleOrder 回收订单信息
     * @return 更新结果
     */
    @PutMapping("/update")
    public BaseResponse<Void> updateRecycleOrder(@RequestBody RecycleOrder recycleOrder) {
        recycleOrderService.updateById(recycleOrder);
        return BaseResponse.success();
    }

    /**
     * 根据 ID 查询回收订单
     * @param id 回收订单 ID
     * @return 回收订单信息
     */
    @GetMapping("/{id}")
    public BaseResponse<RecycleOrder> getRecycleOrder(@PathVariable String id) {
        return BaseResponse.success(recycleOrderService.getById(id));
    }

    /**
     * 查询所有回收订单
     * @return 回收订单列表
     */
    @GetMapping
    public BaseResponse<List<RecycleOrder>> getAllRecycleOrders() {
        return BaseResponse.success(recycleOrderService.list());
    }

    /**
     * 分页查询回收订单
     * @param page 页码
     * @param size 每页数量
     * @return 分页结果
     */
    @GetMapping("/page")
    public BaseResponse<IPage<RecycleOrder>> getRecycleOrdersByPage(@RequestParam(defaultValue = "1") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        Page<RecycleOrder> ipage = new Page<>(page, size);
        Page<RecycleOrder> result = recycleOrderService.page(ipage, new QueryWrapper<>());
        return BaseResponse.success(result);
    }
}
