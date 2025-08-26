package com.tutu.recycle.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleContractItem;
import com.tutu.recycle.mapper.RecycleContractMapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * 回收合同服务实现类
 */
@Service
public class RecycleContractService extends ServiceImpl<RecycleContractMapper, RecycleContract> {
    @Resource
    private RecycleContractItemService recycleContractItemService;
    
    /**
     * 创建合同
     * @param recycleContract
     * @return
     */
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

    /**
     * 更新合同项目明细
     * @param item 合同项目信息明细
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateItem(RecycleContractItem item) {
        recycleContractItemService.updateById(item);
        // 查询合同
        RecycleContract contract = getById(item.getRecycleContractId());
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        // 查询所有明细
        List<RecycleContractItem> items = recycleContractItemService.getItemsByContractId(contract.getId());
        // 更新合同总金额
        updateContractTotalAmount(contract,items);
    }

    /**
     * 创建合同项目明细
     * @param item 合同项目信息明细
     */
    @Transactional(rollbackFor = Exception.class)
    public void createContractItem(RecycleContractItem item) {
        String contractId = item.getRecycleContractId();
        if (contractId == null) {
            throw new RuntimeException("合同ID不能为空");
        }
        RecycleContract contract = getById(contractId);
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        // 保存明细
        recycleContractItemService.save(item);
        // 拿到所有的明细
        List<RecycleContractItem> items = recycleContractItemService.getItemsByContractId(contractId);
        // 更细合同总金额
        updateContractTotalAmount(contract,items);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateContractTotalAmount(RecycleContract contract,List<RecycleContractItem> items) {
        // 计算总金额
        BigDecimal totalAmount = items.stream()
                .map(RecycleContractItem::getGoodTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        // 更新合同总金额
        contract.setTotalAmount(totalAmount);
        updateById(contract);
    }

    /**
     * 删除合同项目明细
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeItem(String id) {
        RecycleContractItem item = recycleContractItemService.getById(id);
        if (item == null) {
            throw new RuntimeException("合同项目不存在");
        }
        RecycleContract contract = getById(item.getRecycleContractId());
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        recycleContractItemService.removeById(id);
        // 重新计算总金额
        List<RecycleContractItem> items = recycleContractItemService.getItemsByContractId(contract.getId());
        updateContractTotalAmount(contract,items);
    }
}