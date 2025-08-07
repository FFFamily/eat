package com.tutu.lease.service;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.lease.dto.AddToCartRequest;
import com.tutu.lease.dto.LeaseCartDto;
import com.tutu.lease.dto.UpdateCartTimeDto;
import com.tutu.lease.entity.LeaseCart;
import com.tutu.lease.entity.LeaseGood;
import com.tutu.lease.mapper.LeaseCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

/**
 * 租赁购物车服务实现类
 */
@Service
public class LeaseCartService extends ServiceImpl<LeaseCartMapper, LeaseCart> {

    @Autowired
    private LeaseGoodService leaseGoodService;

    
    @Transactional(rollbackFor = Exception.class)
    public boolean addToCart(AddToCartRequest request) {
        String userId = StpUtil.getLoginIdAsString();
        
        // 检查商品是否存在
        LeaseGood leaseGood = leaseGoodService.getById(request.getGoodId());
        if (leaseGood == null) {
            throw new RuntimeException("商品不存在");
        }
        
        // 检查商品是否已在购物车中
        LeaseCart existCart = checkGoodInCart(userId, request.getGoodId());
        if (existCart != null) {
            throw new ServiceException("商品已在购物车中");
        }
        
        // 创建新的购物车项目
        LeaseCart cart = new LeaseCart();
        BeanUtil.copyProperties(request, cart);
        cart.setUserId(userId);
        cart.setGoodName(leaseGood.getName());
        cart.setGoodPrice(leaseGood.getPrice());
//        cart.setLeaseDays(calculateLeaseDays(request.getLeaseStartTime(), request.getLeaseEndTime()));
//        cart.setSubtotal(calculateSubtotal(leaseGood.getPrice(), request.getQuantity(), cart.getLeaseDays()));
        cart.setStatus(0); // 正常状态
        
        return save(cart);
    }

    
    public List<LeaseCartDto> getUserCartList(String userId) {
        return baseMapper.selectCartDetailsByUserId(userId, 0);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCartQuantity(String cartId, Integer quantity) {
        String userId = StpUtil.getLoginIdAsString();
        
        LeaseCart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项目不存在");
        }
        
        cart.setQuantity(quantity);
        cart.setSubtotal(calculateSubtotal(cart.getGoodPrice(), quantity, cart.getLeaseDays()));
        
        return updateById(cart);
    }

    
    
    /**
     * 更新购物车租赁时间
     * @param cartId 
     * @param request
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCartLeaseTime(String cartId, UpdateCartTimeDto request) {
        String userId = StpUtil.getLoginIdAsString();
        
        LeaseCart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new ServiceException("购物车项目不存在");
        }
        // 查询购物车中的商品
        LeaseGood leaseGood = leaseGoodService.getById(cart.getGoodId());
        if (leaseGood == null) {
            throw new ServiceException("商品不存在");
        }
        // 拿到商品价格，计算小计金额
        BigDecimal price = leaseGood.getPrice();
        Integer quantity = cart.getQuantity();
        Integer days = calculateLeaseDays(request.getLeaseStartTime(), request.getLeaseEndTime());
        cart.setLeaseStartTime(request.getLeaseStartTime());
        cart.setLeaseEndTime(request.getLeaseEndTime());
        cart.setLeaseDays(days);
        cart.setSubtotal(calculateSubtotal(price, quantity, days));        
        return updateById(cart);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean removeFromCart(String cartId) {
        String userId = StpUtil.getLoginIdAsString();
        
        LeaseCart cart = getById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new RuntimeException("购物车项目不存在");
        }
        
        return removeById(cartId);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean batchRemoveFromCart(List<String> cartIds) {
        String userId = StpUtil.getLoginIdAsString();
        
        // 验证所有购物车项目都属于当前用户
        LambdaQueryWrapper<LeaseCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(LeaseCart::getId, cartIds)
                .eq(LeaseCart::getUserId, userId)
                .eq(LeaseCart::getIsDeleted, CommonConstant.NO_STR);
        
        List<LeaseCart> carts = list(queryWrapper);
        if (carts.size() != cartIds.size()) {
            throw new RuntimeException("部分购物车项目不存在或无权限操作");
        }
        
        return removeByIds(cartIds);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean clearUserCart(String userId) {
        LambdaUpdateWrapper<LeaseCart> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LeaseCart::getUserId, userId)
                .eq(LeaseCart::getStatus, 0)
                .set(LeaseCart::getIsDeleted, CommonConstant.YES_STR);
        
        return update(updateWrapper);
    }

    
    public Integer getUserCartItemCount(String userId) {
        return baseMapper.selectCartItemCountByUserId(userId, 0);
    }

    
    public BigDecimal getUserCartTotalAmount(String userId) {
        LambdaQueryWrapper<LeaseCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseCart::getUserId, userId)
                .eq(LeaseCart::getStatus, 0)
                .eq(LeaseCart::getIsDeleted, CommonConstant.NO_STR);
        
        List<LeaseCart> carts = list(queryWrapper);
        return carts.stream()
                .map(LeaseCart::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    
    public LeaseCart checkGoodInCart(String userId, String goodId) {
        LambdaQueryWrapper<LeaseCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LeaseCart::getUserId, userId)
                .eq(LeaseCart::getGoodId, goodId)
                .eq(LeaseCart::getStatus, 0)
                .eq(LeaseCart::getIsDeleted, CommonConstant.NO_STR);
        
        return getOne(queryWrapper);
    }

    /**
     * 计算租赁天数
     */
    private Integer calculateLeaseDays(Date startTime, Date endTime) {
        LocalDate startDate = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate endDate = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1; // 包含开始和结束日期
    }

    /**
     * 计算小计金额
     * @param price 商品单价
     * @param quantity 租赁数量
     * @param days 租赁天数
     * @return
     */
    private BigDecimal calculateSubtotal(BigDecimal price, Integer quantity, Integer days) {
        return price.multiply(BigDecimal.valueOf(quantity)).multiply(BigDecimal.valueOf(days));
    }
}
