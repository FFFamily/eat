package com.tutu.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.system.entity.SysContractItem;
import com.tutu.system.mapper.SysContractItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 合同子项Service
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class SysContractItemService extends ServiceImpl<SysContractItemMapper, SysContractItem> {

    /**
     * 根据合同ID查询合同子项列表
     * @param contractId 合同ID
     * @return 合同子项列表
     */
    public List<SysContractItem> getByContractId(String contractId) {
        if (!StringUtils.hasText(contractId)) {
            return List.of();
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getContractId, contractId);
        return list(queryWrapper);
    }

    /**
     * 分页查询合同子项
     * @param page 分页对象
     * @param contractItem 查询条件
     * @return 分页结果
     */
    public IPage<SysContractItem> getPage(Page<SysContractItem> page, SysContractItem contractItem) {
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        
        if (contractItem != null) {
            // 根据合同ID查询
            if (StringUtils.hasText(contractItem.getContractId())) {
                queryWrapper.eq(SysContractItem::getContractId, contractItem.getContractId());
            }
            
            // 根据回收货物名称模糊查询
            if (StringUtils.hasText(contractItem.getRecycleGoodName())) {
                queryWrapper.like(SysContractItem::getRecycleGoodName, contractItem.getRecycleGoodName());
            }
            
            // 根据租赁设备名称模糊查询
            if (StringUtils.hasText(contractItem.getLeaseGoodName())) {
                queryWrapper.like(SysContractItem::getLeaseGoodName, contractItem.getLeaseGoodName());
            }
            
            // 根据回收货物ID查询
            if (StringUtils.hasText(contractItem.getRecycleGoodId())) {
                queryWrapper.eq(SysContractItem::getRecycleGoodId, contractItem.getRecycleGoodId());
            }
            
            // 根据租赁设备ID查询
            if (StringUtils.hasText(contractItem.getLeaseGoodId())) {
                queryWrapper.eq(SysContractItem::getLeaseGoodId, contractItem.getLeaseGoodId());
            }
        }
        
        queryWrapper.orderByDesc(SysContractItem::getId);
        return page(page, queryWrapper);
    }

    /**
     * 批量保存合同子项
     * @param contractItems 合同子项列表
     * @return 是否成功
     */
    public boolean saveBatch(List<SysContractItem> contractItems) {
        if (contractItems == null || contractItems.isEmpty()) {
            return false;
        }
        return saveBatch(contractItems);
    }

    /**
     * 根据合同ID删除合同子项
     * @param contractId 合同ID
     * @return 是否成功
     */
    public boolean deleteByContractId(String contractId) {
        if (!StringUtils.hasText(contractId)) {
            return false;
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getContractId, contractId);
        return remove(queryWrapper);
    }

    /**
     * 根据回收货物ID查询合同子项
     * @param recycleGoodId 回收货物ID
     * @return 合同子项列表
     */
    public List<SysContractItem> getByRecycleGoodId(String recycleGoodId) {
        if (!StringUtils.hasText(recycleGoodId)) {
            return List.of();
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getRecycleGoodId, recycleGoodId);
        return list(queryWrapper);
    }

    /**
     * 根据租赁设备ID查询合同子项
     * @param leaseGoodId 租赁设备ID
     * @return 合同子项列表
     */
    public List<SysContractItem> getByLeaseGoodId(String leaseGoodId) {
        if (!StringUtils.hasText(leaseGoodId)) {
            return List.of();
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getLeaseGoodId, leaseGoodId);
        return list(queryWrapper);
    }

    /**
     * 根据合同ID统计合同子项数量
     * @param contractId 合同ID
     * @return 数量
     */
    public long countByContractId(String contractId) {
        if (!StringUtils.hasText(contractId)) {
            return 0;
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getContractId, contractId);
        return count(queryWrapper);
    }

    /**
     * 根据合同ID获取回收货物总金额
     * @param contractId 合同ID
     * @return 总金额
     */
    public List<SysContractItem> getRecycleGoodsByContractId(String contractId) {
        if (!StringUtils.hasText(contractId)) {
            return List.of();
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getContractId, contractId)
                   .isNotNull(SysContractItem::getRecycleGoodId);
        return list(queryWrapper);
    }

    /**
     * 根据合同ID获取租赁设备总金额
     * @param contractId 合同ID
     * @return 总金额
     */
    public List<SysContractItem> getLeaseGoodsByContractId(String contractId) {
        if (!StringUtils.hasText(contractId)) {
            return List.of();
        }
        LambdaQueryWrapper<SysContractItem> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysContractItem::getContractId, contractId)
                   .isNotNull(SysContractItem::getLeaseGoodId);
        return list(queryWrapper);
    }
} 