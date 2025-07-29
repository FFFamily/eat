package com.tutu.lease.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.lease.entity.LeaseGood;
import com.tutu.lease.service.LeaseGoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.tutu.hs_common.common.BaseResponse;

@RestController
@RequestMapping("/lease-good")
public class LeaseGoodController {

    @Autowired
    private LeaseGoodService leaseGoodService;

    @GetMapping
    public BaseResponse<List<LeaseGood>> list() {
        return BaseResponse.success(leaseGoodService.list());
    }

    @GetMapping("/{id}")
    public BaseResponse<LeaseGood> getById(@PathVariable String id) {
        return BaseResponse.success(leaseGoodService.getById(id));
    }

    @PostMapping
    public BaseResponse<Boolean> save(@RequestBody LeaseGood leaseGood) {
        return BaseResponse.success(leaseGoodService.save(leaseGood));
    }

    @PutMapping
    public BaseResponse<Boolean> update(@RequestBody LeaseGood leaseGood) {
        return BaseResponse.success(leaseGoodService.updateById(leaseGood));
    }

    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> remove(@PathVariable String id) {
        return BaseResponse.success(leaseGoodService.removeById(id));
    }

    @GetMapping("/page")
    public BaseResponse<Page<LeaseGood>> page(@RequestParam int current, @RequestParam int size) {
        Page<LeaseGood> page = new Page<>(current, size);
        return BaseResponse.success(leaseGoodService.page(page));
    }
}