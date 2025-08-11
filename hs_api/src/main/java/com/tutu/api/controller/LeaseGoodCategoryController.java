package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.common.constant.CommonConstant;
import com.tutu.lease.entity.LeaseGoodCategory;
import com.tutu.lease.service.LeaseGoodCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租赁商品分类控制器
 */
@RestController
@RequestMapping("/lease/good/category")
public class LeaseGoodCategoryController {

    @Autowired
    private LeaseGoodCategoryService leaseGoodCategoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping("/list")
    public BaseResponse<List<LeaseGoodCategory>> list() {
        LambdaQueryWrapper<LeaseGoodCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(LeaseGoodCategory::getCreateTime);
        return BaseResponse.success(leaseGoodCategoryService.list(queryWrapper));
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public BaseResponse<LeaseGoodCategory> getById(@PathVariable String id) {
        return BaseResponse.success(leaseGoodCategoryService.getById(id));
    }

    /**
     * 创建分类
     */
    @PostMapping("/create")
    public BaseResponse<Boolean> save(@RequestBody LeaseGoodCategory category) {
        return BaseResponse.success(leaseGoodCategoryService.createCategory(category));
    }

    /**
     * 更新分类
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody LeaseGoodCategory category) {
        return BaseResponse.success(leaseGoodCategoryService.updateCategory(category));
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> remove(@PathVariable String id) {
        return BaseResponse.success(leaseGoodCategoryService.removeById(id));
    }

    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public BaseResponse<Page<LeaseGoodCategory>> page(@RequestParam int pageNum, @RequestParam int pageSize,
                                                      @RequestParam(required = false) String name) {
        Page<LeaseGoodCategory> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<LeaseGoodCategory> queryWrapper = new LambdaQueryWrapper<>();
        // 如果有关键字，按名称模糊查询
        if (name != null && !name.trim().isEmpty()) {
            queryWrapper.like(LeaseGoodCategory::getName, name);
        }
        queryWrapper.orderByDesc(LeaseGoodCategory::getCreateTime);
        return BaseResponse.success(leaseGoodCategoryService.page(page, queryWrapper));
    }
}
