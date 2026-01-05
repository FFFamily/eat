package com.tutu.point.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.point.constant.UserPointLockConstant;
import com.tutu.point.entity.PointGoods;
import com.tutu.point.mapper.PointGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 积分商品 Service
 */
@Service
public class PointGoodsService extends ServiceImpl<PointGoodsMapper, PointGoods> {
    
    @Autowired
    private PointGoodsTypeService pointGoodsTypeService;
    
    /**
     * 创建商品
     */
    public PointGoods createGoods(PointGoods goods) {
        if (StrUtil.isBlank(goods.getGoodsName())) {
            throw new ServiceException("商品名称不能为空");
        }
        
        if (StrUtil.isBlank(goods.getTypeId())) {
            throw new ServiceException("商品分类不能为空");
        }
        
        // 校验分类是否存在
        if (pointGoodsTypeService.getById(goods.getTypeId()) == null) {
            throw new ServiceException("商品分类不存在");
        }
        
        if (goods.getPointPrice() == null || goods.getPointPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("积分价格必须大于0");
        }
        
        // 设置默认状态
        if (StrUtil.isBlank(goods.getStatus())) {
            goods.setStatus("1");
        }
        
        // 设置默认库存
        if (goods.getStock() == null) {
            goods.setStock(0);
        }
        
        // 设置默认排序
        if (goods.getSortNum() == null) {
            goods.setSortNum(0);
        }
        
        save(goods);
        return goods;
    }
    
    /**
     * 更新商品
     */
    public PointGoods updateGoods(PointGoods goods) {
        if (StrUtil.isBlank(goods.getId())) {
            throw new ServiceException("商品ID不能为空");
        }
        
        PointGoods existing = getById(goods.getId());
        if (existing == null) {
            throw new ServiceException("商品不存在");
        }
        
        if (StrUtil.isNotBlank(goods.getTypeId()) && !goods.getTypeId().equals(existing.getTypeId())) {
            // 校验分类是否存在
            if (pointGoodsTypeService.getById(goods.getTypeId()) == null) {
                throw new ServiceException("商品分类不存在");
            }
        }
        
        if (goods.getPointPrice() != null && goods.getPointPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("积分价格必须大于0");
        }
        
        updateById(goods);
        return goods;
    }
    
    /**
     * 上架/下架商品
     */
    public void updateGoodsStatus(String goodsId, String status) {
        PointGoods goods = getById(goodsId);
        if (goods == null) {
            throw new ServiceException("商品不存在");
        }
        
        goods.setStatus(status);
        updateById(goods);
    }
    
    /**
     * 分页查询商品（带分类名称）
     */
    public Page<PointGoods> pageGoods(Integer page, Integer size, String goodsName, String typeId, String status) {
        Page<PointGoods> pageObj = new Page<>(page, size);
        return baseMapper.pageGoodsWithTypeName(pageObj, goodsName, typeId, status);
    }
    
    /**
     * 根据分类ID获取商品列表
     */
    public List<PointGoods> getGoodsByTypeId(String typeId) {
        LambdaQueryWrapper<PointGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointGoods::getTypeId, typeId);
        wrapper.eq(PointGoods::getStatus, "1");
        wrapper.orderByAsc(PointGoods::getSortNum);
        return list(wrapper);
    }
    
    /**
     * 获取所有上架的商品
     */
    public List<PointGoods> listActiveGoods() {
        LambdaQueryWrapper<PointGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PointGoods::getStatus, "1");
        wrapper.orderByAsc(PointGoods::getSortNum);
        return list(wrapper);
    }
    
    /**
     * 减少库存（带锁，防止超卖）
     */
    @Transactional(rollbackFor = Exception.class)
    public void decreaseStock(String goodsId, Integer quantity) {
        if (StrUtil.isBlank(goodsId)) {
            throw new ServiceException("商品ID不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new ServiceException("减少库存数量必须大于0");
        }
        
        // 使用商品ID作为锁键，确保同一商品的库存操作串行执行
        synchronized ((UserPointLockConstant.GOODS_STOCK_LOCK_KEY + goodsId).intern()) {
            // 在锁内重新查询商品，确保获取最新的库存数据
            PointGoods goods = getById(goodsId);
            if (goods == null) {
                throw new ServiceException("商品不存在");
            }
            
            // 再次检查库存是否充足（防止并发超卖）
            if (goods.getStock() == null || goods.getStock() < quantity) {
                throw new ServiceException("库存不足，当前库存：" + (goods.getStock() == null ? 0 : goods.getStock()) + "，需要数量：" + quantity);
            }
            
            // 更新库存
            goods.setStock(goods.getStock() - quantity);
            updateById(goods);
        }
    }
    
    /**
     * 增加库存
     */
    public void increaseStock(String goodsId, Integer quantity) {
        PointGoods goods = getById(goodsId);
        if (goods == null) {
            throw new ServiceException("商品不存在");
        }
        
        if (goods.getStock() == null) {
            goods.setStock(0);
        }
        
        goods.setStock(goods.getStock() + quantity);
        updateById(goods);
    }
}

