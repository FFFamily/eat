package com.tutu.recycle.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.mapper.RecycleContractMapper;
import org.springframework.stereotype.Service;

/**
 * 回收合同服务实现类
 */
@Service
public class RecycleContractService extends ServiceImpl<RecycleContractMapper, RecycleContract> {
    
    
    public RecycleContract createContract(RecycleContract recycleContract) {
        // 保存合同
        save(recycleContract);
        return recycleContract;
    }
    
    
    public boolean updateContractStatus(String id, String status) {
        RecycleContract contract = getById(id);
        if (contract != null) {
            // 这里可以添加状态验证逻辑
            return updateById(contract);
        }
        return false;
    }
} 