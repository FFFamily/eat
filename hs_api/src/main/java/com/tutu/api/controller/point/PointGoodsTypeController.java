package com.tutu.api.controller.point;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.point.entity.PointGoodsType;
import com.tutu.point.service.PointGoodsTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分商品分类 Controller
 */
@RestController
@RequestMapping("/point/goods-type")
public class PointGoodsTypeController {
    
    @Autowired
    private PointGoodsTypeService pointGoodsTypeService;
    
    /**
     * 创建分类
     */
    @PostMapping("/create")
    public BaseResponse<PointGoodsType> create(@RequestBody PointGoodsType type) {
        return BaseResponse.success(pointGoodsTypeService.createType(type));
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/update")
    public BaseResponse<PointGoodsType> update(@RequestBody PointGoodsType type) {
        return BaseResponse.success(pointGoodsTypeService.updateType(type));
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(pointGoodsTypeService.removeById(id));
    }
    
    /**
     * 根据ID查询分类
     */
    @GetMapping("/{id}")
    public BaseResponse<PointGoodsType> getById(@PathVariable String id) {
        return BaseResponse.success(pointGoodsTypeService.getById(id));
    }
    
    /**
     * 分页查询分类
     */
    @GetMapping("/page")
    public BaseResponse<Page<PointGoodsType>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false) String status) {
        return BaseResponse.success(pointGoodsTypeService.pageType(page, size, typeName, status));
    }
    
    /**
     * 获取所有启用的分类
     */
    @GetMapping("/list/active")
    public BaseResponse<List<PointGoodsType>> listActive() {
        return BaseResponse.success(pointGoodsTypeService.listActiveTypes());
    }
    
    /**
     * 启用/禁用分类
     */
    @PutMapping("/status")
    public BaseResponse<Void> updateStatus(@RequestParam String id, @RequestParam String status) {
        pointGoodsTypeService.updateTypeStatus(id, status);
        return BaseResponse.success();
    }
}

