package com.tutu.api.controller.pool;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleCapitalPoolItem;
import com.tutu.recycle.service.RecycleCapitalPoolItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle/capital-pool-item")
public class RecycleCapitalPoolItemController {

    @Autowired
    private RecycleCapitalPoolItemService recycleCapitalPoolItemService;

    @PostMapping("/create")
    public BaseResponse<Boolean> add(@RequestBody RecycleCapitalPoolItem entity) {
        return BaseResponse.success(recycleCapitalPoolItemService.save(entity));
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody RecycleCapitalPoolItem entity) {
        return BaseResponse.success(recycleCapitalPoolItemService.updateById(entity));
    }

    @GetMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(recycleCapitalPoolItemService.removeById(id));
    }

    @GetMapping("/get/{id}")
    public BaseResponse<RecycleCapitalPoolItem> getById(@PathVariable String id) {
        return BaseResponse.success(recycleCapitalPoolItemService.getById(id));
    }


    @PostMapping("/page")
    public BaseResponse<Page<RecycleCapitalPoolItem>> page(@RequestParam(defaultValue = "1") Integer current,
                                                           @RequestParam(defaultValue = "10") Integer size,
                                                           @RequestBody(required = false) RecycleCapitalPoolItem query) {
        Page<RecycleCapitalPoolItem> page = new Page<>(current, size);
        QueryWrapper<RecycleCapitalPoolItem> queryWrapper = new QueryWrapper<>();
        if (query != null) {
            if (query.getCapitalPoolId() != null) {
                queryWrapper.eq("capital_pool_id", query.getCapitalPoolId());
            }
            if (query.getOrderId() != null) {
                queryWrapper.eq("order_id", query.getOrderId());
            }
            if (query.getDirection() != null) {
                queryWrapper.eq("direction", query.getDirection());
            }
        }
        queryWrapper.orderByDesc("create_time");
        return BaseResponse.success(recycleCapitalPoolItemService.page(page, queryWrapper));
    }
}

