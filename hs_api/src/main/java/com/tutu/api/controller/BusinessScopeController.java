package com.tutu.api.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.recycle.entity.BusinessScope;
import com.tutu.recycle.service.BusinessScopeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/recycle/business-scope")
public class BusinessScopeController {

    @Autowired
    private BusinessScopeService businessScopeService;

    /**
     * 获取所有经营范围
     */
    @GetMapping("/list")
    public BaseResponse<List<BusinessScope>> list() {
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(BusinessScope::getCreateTime);
        return BaseResponse.success(businessScopeService.list(wrapper));
    }

    /**
     * 根据ID获取经营范围
     */
    @GetMapping("/{id}")
    public BaseResponse<BusinessScope> getById(@PathVariable String id) {
        return BaseResponse.success(businessScopeService.getById(id));
    }

    /**
     * 根据货物类型查询经营范围
     */
    @GetMapping("/type/{goodType}")
    public BaseResponse<List<BusinessScope>> getByGoodType(@PathVariable String goodType) {
        return BaseResponse.success(businessScopeService.getByGoodType(goodType));
    }

    /**
     * 根据货物名称搜索经营范围
     */
    @GetMapping("/search")
    public BaseResponse<List<BusinessScope>> searchByGoodName(@RequestParam String goodName) {
        return BaseResponse.success(businessScopeService.searchByGoodName(goodName));
    }

    /**
     * 分页查询经营范围
     */
    @GetMapping("/page")
    public BaseResponse<Page<BusinessScope>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Page<BusinessScope> page = new Page<>(pageNum, pageSize);
        return BaseResponse.success(businessScopeService.page(page));
    }

    /**
     * 创建经营范围
     */
    @PostMapping("/create")
    public BaseResponse<BusinessScope> create(@RequestBody BusinessScope businessScope) {
        return BaseResponse.success(businessScopeService.createBusinessScope(businessScope));
    }

    /**
     * 更新经营范围
     */
    @PutMapping("/update")
    public BaseResponse<Boolean> update(@RequestBody BusinessScope businessScope) {
        return BaseResponse.success(businessScopeService.updateBusinessScope(businessScope));
    }

    /**
     * 删除经营范围
     */
    @DeleteMapping("/delete/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(businessScopeService.deleteBusinessScope(id));
    }

    /**
     * 更新是否显示
     */
    @PutMapping("/update/isShow")
    public BaseResponse<Boolean> updateIsShow(@RequestBody BusinessScope businessScope) {
        businessScopeService.updateIsShow(businessScope);
        return BaseResponse.success();
    }
} 