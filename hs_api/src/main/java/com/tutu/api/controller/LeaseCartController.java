package com.tutu.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.tutu.common.Response.BaseResponse;
import com.tutu.lease.dto.AddToCartRequest;
import com.tutu.lease.dto.LeaseCartDto;
import com.tutu.lease.dto.UpdateCartTimeDto;
import com.tutu.lease.service.LeaseCartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 租赁购物车控制器
 */
@RestController
@RequestMapping("/lease/cart")
public class LeaseCartController {

    @Autowired
    private LeaseCartService leaseCartService;

    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public BaseResponse<Void> addToCart(@RequestBody AddToCartRequest request) {
        leaseCartService.addToCart(request);
        return BaseResponse.success();
    }

    /**
     * 获取当前用户购物车列表
     */
    @GetMapping("/list")
    public BaseResponse<List<LeaseCartDto>> getCartList() {
        String userId = StpUtil.getLoginIdAsString();
        return BaseResponse.success(leaseCartService.getUserCartList(userId));
    }

    /**
     * 更新购物车商品数量
     */
    @PutMapping("/quantity/{cartId}")
    public BaseResponse<Boolean> updateQuantity(@PathVariable String cartId, 
                                              @RequestParam Integer quantity) {
        return BaseResponse.success(leaseCartService.updateCartQuantity(cartId, quantity));
    }

    /**
     * 更新购物车租赁时间
     */
    @PutMapping("/lease-time/{cartId}")
    public BaseResponse<Void> updateLeaseTime(@PathVariable String cartId, 
                                               @RequestBody @Valid UpdateCartTimeDto request) {
        leaseCartService.updateCartLeaseTime(cartId, request);
        return BaseResponse.success();
    }

    /**
     * 删除购物车商品
     */
    @DeleteMapping("/{cartId}")
    public BaseResponse<Boolean> removeFromCart(@PathVariable String cartId) {
        return BaseResponse.success(leaseCartService.removeFromCart(cartId));
    }

    /**
     * 批量删除购物车商品
     */
    @DeleteMapping("/batch")
    public BaseResponse<Boolean> batchRemoveFromCart(@RequestBody List<String> cartIds) {
        return BaseResponse.success(leaseCartService.batchRemoveFromCart(cartIds));
    }

    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public BaseResponse<Boolean> clearCart() {
        String userId = StpUtil.getLoginIdAsString();
        return BaseResponse.success(leaseCartService.clearUserCart(userId));
    }

    /**
     * 获取购物车商品总数量
     */
    @GetMapping("/count")
    public BaseResponse<Integer> getCartItemCount() {
        String userId = StpUtil.getLoginIdAsString();
        return BaseResponse.success(leaseCartService.getUserCartItemCount(userId));
    }

    /**
     * 获取购物车总金额
     */
    @GetMapping("/total-amount")
    public BaseResponse<BigDecimal> getCartTotalAmount() {
        String userId = StpUtil.getLoginIdAsString();
        return BaseResponse.success(leaseCartService.getUserCartTotalAmount(userId));
    }

    /**
     * 获取购物车统计信息
     */
    @GetMapping("/statistics")
    public BaseResponse<Map<String, Object>> getCartStatistics() {
        String userId = StpUtil.getLoginIdAsString();
        Integer itemCount = leaseCartService.getUserCartItemCount(userId);
        BigDecimal totalAmount = leaseCartService.getUserCartTotalAmount(userId);
        
        Map<String, Object> statistics = Map.of(
            "itemCount", itemCount != null ? itemCount : 0,
            "totalAmount", totalAmount != null ? totalAmount : BigDecimal.ZERO
        );
        
        return BaseResponse.success(statistics);
    }
}
