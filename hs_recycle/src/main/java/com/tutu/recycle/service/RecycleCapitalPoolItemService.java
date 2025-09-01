package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleCapitalPoolItem;
import com.tutu.recycle.mapper.RecycleCapitalPoolItemMapper;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class RecycleCapitalPoolItemService extends ServiceImpl<RecycleCapitalPoolItemMapper, RecycleCapitalPoolItem> {

    /**
     * 获取资金池下项目
     * @param poolId
     * @return
     */
    public List<RecycleCapitalPoolItem> getPoolByPoolId(String poolId) {
        return this.list(new LambdaQueryWrapper<RecycleCapitalPoolItem>()
        .eq(RecycleCapitalPoolItem::getCapitalPoolId, poolId));
    }
}

