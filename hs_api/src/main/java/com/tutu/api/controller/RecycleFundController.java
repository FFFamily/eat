package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.service.RecycleContractService;
import com.tutu.recycle.service.RecycleFundService;
import com.tutu.recycle.service.RecycleOrderService;

import jakarta.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle/fund")
public class RecycleFundController {

    @Autowired
    private RecycleFundService recycleFundService;
    @Resource
    private RecycleContractService recycleContractService;

    /**
     * 新增走款记录
     * @param recycleFund
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody RecycleFund recycleFund) {
        recycleFundService.create(recycleFund);
        return BaseResponse.success();
    }
    /**
     * 批量新增走款记录
     * @param recycleFundList 走款记录列表
     * @return
     */
    @PostMapping("/addBatch")
    public BaseResponse<Boolean> addBatch(@RequestBody List<RecycleFund> recycleFundList) {
        recycleFundService.createBatch(recycleFundList);
        return BaseResponse.success();
    }

    /**
     * 更新走款记录
     * @param recycleFund
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody RecycleFund recycleFund) {
        return BaseResponse.success(recycleFundService.updateById(recycleFund));
    }

    /**
     * 删除走款记录
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(recycleFundService.removeById(id));
    }

    /**
     * 获取走款记录
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public BaseResponse<RecycleFund> getById(@PathVariable String id) {
        return BaseResponse.success(recycleFundService.getById(id));
    }

    /**
     * 分页获取走款记录
     * @param page
     * @param size
     * @param query
     * @return
     */
    @PostMapping("/page")
    public BaseResponse<Page<RecycleFund>> page(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestBody(required = false) RecycleFund query) {
        Page<RecycleFund> ipage = new Page<>(page, size);
        QueryWrapper<RecycleFund> queryWrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getNo() != null) {
                queryWrapper.like("no", query.getNo());
            }
            if (query.getContractNo() != null) {
                queryWrapper.like("contract_no", query.getContractNo());
            }
            if (query.getOrderNo() != null) {
                queryWrapper.like("order_no", query.getOrderNo());
            }
            if (query.getPartner() != null) {
                queryWrapper.like("partner", query.getPartner());
            }
        }
        queryWrapper.orderByDesc("create_time");
        Page<RecycleFund> result = recycleFundService.page(ipage, queryWrapper);
        List<RecycleFund> records = result.getRecords();
        if (!records.isEmpty()){
            List<String> ids = records.stream().map(RecycleFund::getContractNo).collect(Collectors.toList());
            Map<String, RecycleContract> contractMap = recycleContractService.list(new LambdaQueryWrapper<RecycleContract>().in(RecycleContract::getNo, ids)).stream().collect(Collectors.toMap(RecycleContract::getNo, i -> i));
            result.getRecords().forEach(i -> {
                RecycleContract contract = contractMap.get(i.getContractNo());
                if (contract != null) {
                    i.setContractStartTime(contract.getStartTime());
                    i.setContractEndTime(contract.getEndTime());
                }
            });
        }
        return BaseResponse.success(result);
    }
} 