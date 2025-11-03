package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleContractBeneficiary;
import com.tutu.recycle.mapper.RecycleContractBeneficiaryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 回收合同受益人服务实现类
 */
@Service
public class RecycleContractBeneficiaryService extends ServiceImpl<RecycleContractBeneficiaryMapper, RecycleContractBeneficiary> {
    
    /**
     * 根据合同ID查询受益人列表
     * @param contractId 合同ID
     * @return 受益人列表
     */
    public List<RecycleContractBeneficiary> getBeneficiariesByContractId(String contractId) {
        LambdaQueryWrapper<RecycleContractBeneficiary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleContractBeneficiary::getContractId, contractId);
        wrapper.orderByAsc(RecycleContractBeneficiary::getBeneficiaryType);
        return list(wrapper);
    }
    
    /**
     * 批量保存受益人
     * @param beneficiaries 受益人列表
     * @return 保存结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean batchSaveBeneficiaries(List<RecycleContractBeneficiary> beneficiaries) {
        return saveBatch(beneficiaries);
    }
    
    /**
     * 根据合同ID删除所有受益人
     * @param contractId 合同ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void removeByContractId(String contractId) {
        LambdaQueryWrapper<RecycleContractBeneficiary> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RecycleContractBeneficiary::getContractId, contractId);
        remove(wrapper);
    }
    
    /**
     * 保存或更新受益人列表（先删除旧的，再保存新的）
     * @param contractId 合同ID
     * @param beneficiaries 受益人列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateBeneficiaries(String contractId, List<RecycleContractBeneficiary> beneficiaries) {
        // 先删除该合同的所有受益人
        removeByContractId(contractId);
        // 如果有新的受益人，则保存
        if (beneficiaries != null && !beneficiaries.isEmpty()) {
            for (RecycleContractBeneficiary beneficiary : beneficiaries) {
                beneficiary.setContractId(contractId);
            }
            batchSaveBeneficiaries(beneficiaries);
        }
    }
}

