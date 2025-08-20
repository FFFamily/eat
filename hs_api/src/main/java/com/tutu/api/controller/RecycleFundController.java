package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.service.RecycleFundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle/fund")
public class RecycleFundController {

    @Autowired
    private RecycleFundService recycleFundService;

    @PostMapping("/add")
    public BaseResponse<Boolean> add(@RequestBody RecycleFund recycleFund) {
        return BaseResponse.success(recycleFundService.save(recycleFund));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody RecycleFund recycleFund) {
        return BaseResponse.success(recycleFundService.updateById(recycleFund));
    }

    @GetMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(recycleFundService.removeById(id));
    }

    @GetMapping("/get/{id}")
    public BaseResponse<RecycleFund> getById(@PathVariable String id) {
        return BaseResponse.success(recycleFundService.getById(id));
    }

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
        return BaseResponse.success(recycleFundService.page(ipage, queryWrapper));
    }
} 