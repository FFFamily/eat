package com.tutu.api.controller.pool;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.service.RecycleCapitalPoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle/capital-pool")
public class RecycleCapitalPoolController {

    @Autowired
    private RecycleCapitalPoolService recycleCapitalPoolService;

    @PostMapping("/create")
    public BaseResponse<Boolean> add(@RequestBody RecycleCapitalPool entity) {
        recycleCapitalPoolService.create(entity);
        return BaseResponse.success();
    }

    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody RecycleCapitalPool entity) {
        return BaseResponse.success(recycleCapitalPoolService.updateById(entity));
    }

    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        recycleCapitalPoolService.deletePool(id);
        return BaseResponse.success();
    }

    @GetMapping("/get/{id}")
    public BaseResponse<RecycleCapitalPool> getById(@PathVariable String id) {
        return BaseResponse.success(recycleCapitalPoolService.getById(id));
    }

    @PostMapping("/page")
    public BaseResponse<Page<RecycleCapitalPool>> page(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       @RequestBody(required = false) RecycleCapitalPool query) {
        Page<RecycleCapitalPool> ipage = new Page<>(page, size);
        QueryWrapper<RecycleCapitalPool> queryWrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getNo() != null) {
                queryWrapper.like("no", query.getNo());
            }
            if (query.getContractNo() != null) {
                queryWrapper.eq("contract_no", query.getContractNo());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return BaseResponse.success(recycleCapitalPoolService.page(ipage, queryWrapper));
    }
}

