package com.tutu.api.controller.pool;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleCapitalPoolItem;
import com.tutu.recycle.service.RecycleCapitalPoolService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recycle/capital-pool")
public class RecycleCapitalPoolController {

    @Autowired
    private RecycleCapitalPoolService recycleCapitalPoolService;

    /**
     * 新增资金池
     * @param entity
     * @return
     */
    @PostMapping("/create")
    public BaseResponse<Boolean> add(@RequestBody RecycleCapitalPool entity) {
        recycleCapitalPoolService.create(entity);
        return BaseResponse.success();
    }
    /**
     * 获取资金池明细
     * @param id
     * @return
     */
    @GetMapping("/item/get/{capitalPoolId}")
    public BaseResponse<List<RecycleCapitalPoolItem>> getItem(@PathVariable String capitalPoolId) {
        return BaseResponse.success(recycleCapitalPoolService.getItem(capitalPoolId));
    }

    /**
     * 根据合同编号获取资金池
     * @param contractNo
     * @return
     */
    @GetMapping("/getByContractNo/{contractNo}")
    public BaseResponse<RecycleCapitalPool> getByContractNo(@PathVariable String contractNo) {
        return BaseResponse.success(recycleCapitalPoolService.getByContractNo(contractNo));
    }

    /**
     * 更新资金池
     * @param entity
     * @return
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody RecycleCapitalPool entity) {
        return BaseResponse.success(recycleCapitalPoolService.updateById(entity));
    }

    /**
     * 删除资金池
     * @param id
     * @return
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        recycleCapitalPoolService.deletePool(id);
        return BaseResponse.success();
    }

    /**
     * 根据id获取资金池（带合同和账户信息）
     * @param id
     * @return
     */
    @GetMapping("/get/{id}")
    public BaseResponse<RecycleCapitalPool> getById(@PathVariable String id) {
        return BaseResponse.success(recycleCapitalPoolService.getByIdWithDetails(id));
    }

    /**
     * 分页获取资金池（带合同和账户信息）
     * @param page
     * @param size
     * @param query
     * @return
     */
    @PostMapping("/page")
    public BaseResponse<Page<RecycleCapitalPool>> page(@RequestParam(defaultValue = "1") Integer page,
                                                       @RequestParam(defaultValue = "10") Integer size,
                                                       @RequestBody(required = false) RecycleCapitalPool query) {
        Page<RecycleCapitalPool> ipage = new Page<>(page, size);
        return BaseResponse.success(recycleCapitalPoolService.pageWithDetails(ipage, query));
    }
}

