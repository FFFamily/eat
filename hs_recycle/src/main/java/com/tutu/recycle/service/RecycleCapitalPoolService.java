package com.tutu.recycle.service;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleCapitalPoolItem;
import com.tutu.recycle.mapper.RecycleCapitalPoolMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;

@Service
public class RecycleCapitalPoolService extends ServiceImpl<RecycleCapitalPoolMapper, RecycleCapitalPool> {
    
    @Resource
    private RecycleCapitalPoolItemService itemService;
    /**
     * 新增资金池
     * @param entity
     */
    public void create(RecycleCapitalPool entity) {
        // 编号唯一
        String no = entity.getNo();
        RecycleCapitalPool one = getOne(new LambdaQueryWrapper<RecycleCapitalPool>().eq(RecycleCapitalPool::getNo, no));
        if (one != null) {
            throw new RuntimeException("编号已存在");
        }
        // 计算余额
        entity.setBalance(entity.getInitialAmount());
        // 保存
        this.save(entity);
    }

    /**
     * 删除资金池
     * @param id
     */
    public void deletePool(String id) {
        List<RecycleCapitalPoolItem> items = itemService.getPoolByPoolId(id);
        if (items.size() > 0) {
            throw new RuntimeException("资金池下有明细，不能删除");
        }
        this.removeById(id);
    }

}

