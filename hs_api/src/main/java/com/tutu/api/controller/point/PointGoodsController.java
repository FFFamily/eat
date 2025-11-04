package com.tutu.api.controller.point;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.common.Response.BaseResponse;
import com.tutu.point.entity.PointGoods;
import com.tutu.point.service.PointGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分商品 Controller
 */
@RestController
@RequestMapping("/point/goods")
public class PointGoodsController {
    
    @Autowired
    private PointGoodsService pointGoodsService;
    
    /**
     * 创建商品
     */
    @PostMapping("/create")
    public BaseResponse<PointGoods> create(@RequestBody PointGoods goods) {
        return BaseResponse.success(pointGoodsService.createGoods(goods));
    }
    
    /**
     * 更新商品
     */
    @PutMapping("/update")
    public BaseResponse<PointGoods> update(@RequestBody PointGoods goods) {
        return BaseResponse.success(pointGoodsService.updateGoods(goods));
    }
    
    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public BaseResponse<Boolean> delete(@PathVariable String id) {
        return BaseResponse.success(pointGoodsService.removeById(id));
    }
    
    /**
     * 根据ID查询商品
     */
    @GetMapping("/{id}")
    public BaseResponse<PointGoods> getById(@PathVariable String id) {
        return BaseResponse.success(pointGoodsService.getById(id));
    }
    
    /**
     * 分页查询商品
     */
    @GetMapping("/page")
    public BaseResponse<Page<PointGoods>> page(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String goodsName,
            @RequestParam(required = false) String typeId,
            @RequestParam(required = false) String status) {
        return BaseResponse.success(pointGoodsService.pageGoods(page, size, goodsName, typeId, status));
    }
    
    /**
     * 获取所有上架的商品
     */
    @GetMapping("/list/active")
    public BaseResponse<List<PointGoods>> listActive() {
        return BaseResponse.success(pointGoodsService.listActiveGoods());
    }
    
    /**
     * 根据分类ID获取商品列表
     */
    @GetMapping("/list/type/{typeId}")
    public BaseResponse<List<PointGoods>> getByTypeId(@PathVariable String typeId) {
        return BaseResponse.success(pointGoodsService.getGoodsByTypeId(typeId));
    }
    
    /**
     * 上架/下架商品
     */
    @PutMapping("/status")
    public BaseResponse<Void> updateStatus(@RequestParam String id, @RequestParam String status) {
        pointGoodsService.updateGoodsStatus(id, status);
        return BaseResponse.success();
    }
    
    /**
     * 减少库存
     */
    @PutMapping("/stock/decrease")
    public BaseResponse<Void> decreaseStock(@RequestParam String id, @RequestParam Integer quantity) {
        pointGoodsService.decreaseStock(id, quantity);
        return BaseResponse.success();
    }
    
    /**
     * 增加库存
     */
    @PutMapping("/stock/increase")
    public BaseResponse<Void> increaseStock(@RequestParam String id, @RequestParam Integer quantity) {
        pointGoodsService.increaseStock(id, quantity);
        return BaseResponse.success();
    }
}

