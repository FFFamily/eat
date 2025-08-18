package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleContractItem;
import com.tutu.recycle.mapper.RecycleContractItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 回收合同项目服务实现类
 */
@Service
public class RecycleContractItemService extends ServiceImpl<RecycleContractItemMapper, RecycleContractItem>  {
    

    public List<RecycleContractItem> getItemsByContractId(String contractId) {
        LambdaQueryWrapper<RecycleContractItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleContractItem::getRecycleContractId, contractId);
        return list(wrapper);
    }
    

    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveItems(List<RecycleContractItem> items) {
        return saveBatch(items);
    }
} 