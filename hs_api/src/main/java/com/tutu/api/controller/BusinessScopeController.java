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
     * 获取所有经营范围（按排序号排序）
     */
    @GetMapping("/list")
    public BaseResponse<List<BusinessScope>> list() {
        return BaseResponse.success(businessScopeService.getAllOrdered());
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
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String goodType,
            @RequestParam(required = false) String goodName) {
        Page<BusinessScope> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<BusinessScope> wrapper = new LambdaQueryWrapper<>();
        
        // 货物类型模糊查询
        if (goodType != null && !goodType.trim().isEmpty()) {
            wrapper.like(BusinessScope::getGoodType, goodType.trim());
        }
        
        // 货物名称模糊查询
        if (goodName != null && !goodName.trim().isEmpty()) {
            wrapper.like(BusinessScope::getGoodName, goodName.trim());
        }
        
        wrapper.orderByAsc(BusinessScope::getSortNum);
        return BaseResponse.success(businessScopeService.page(page, wrapper));
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
     * 批量删除经营范围
     */
    @DeleteMapping("/delete/batch")
    public BaseResponse<Boolean> deleteBatch(@RequestBody List<String> ids) {
        return BaseResponse.success(businessScopeService.removeByIds(ids));
    }

    /**
     * 更新是否显示
     */
    @PutMapping("/update/isShow")
    public BaseResponse<Boolean> updateIsShow(@RequestBody BusinessScope businessScope) {
        businessScopeService.updateIsShow(businessScope);
        return BaseResponse.success();
    }
    
    /**
     * 上移经营范围
     */
    @PutMapping("/move-up/{id}")
    public BaseResponse<Boolean> moveUp(@PathVariable String id) {
        try {
            businessScopeService.moveUp(id);
            return BaseResponse.success();
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
    
    /**
     * 下移经营范围
     */
    @PutMapping("/move-down/{id}")
    public BaseResponse<Boolean> moveDown(@PathVariable String id) {
        try {
            businessScopeService.moveDown(id);
            return BaseResponse.success();
        } catch (Exception e) {
            return BaseResponse.error(e.getMessage());
        }
    }
} 