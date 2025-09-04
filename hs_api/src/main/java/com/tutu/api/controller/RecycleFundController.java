package com.tutu.api.controller;

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
     * 确认走款
     * @param recycleFund 走款记录
     * @return
     */
    @PostMapping("/confirm")
    public BaseResponse<Boolean> confirm(@RequestBody RecycleFund recycleFund) {
        recycleFundService.confirm(recycleFund);
        return BaseResponse.success();
    }
    /**
     * 批量新增走款记录
     * @param recycleFundList 走款记录列表
     * @return
     */
    @PostMapping("/addBatch")
    public BaseResponse<Boolean> addBatch(@RequestBody java.util.List<RecycleFund> recycleFundList) {
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
     * 分页获取走款记录（使用XML查询）
     * @param page 页码
     * @param size 每页大小
     * @param query 查询条件
     * @return 分页结果
     */
    @PostMapping("/page")
    public BaseResponse<Page<RecycleFund>> page(@RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer size,
                                         @RequestBody(required = false) RecycleFund query) {
        // 创建分页对象
        Page<RecycleFund> pageParam = new Page<>(page, size);
        
        // 使用XML查询，自动包含合同信息
        Page<RecycleFund> result = recycleFundService.pageWithContract(pageParam, query);
        
        return BaseResponse.success(result);
    }
} 