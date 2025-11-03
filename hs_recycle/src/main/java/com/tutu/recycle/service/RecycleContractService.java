package com.tutu.recycle.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleContractBeneficiary;
import com.tutu.recycle.entity.RecycleContractItem;
import com.tutu.recycle.mapper.RecycleContractMapper;
import com.tutu.user.entity.Account;
import com.tutu.user.service.AccountService;
import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 回收合同服务实现类
 */
@Service
public class RecycleContractService extends ServiceImpl<RecycleContractMapper, RecycleContract> {
    @Resource
    private RecycleContractItemService recycleContractItemService;
    
    @Resource
    private RecycleContractBeneficiaryService recycleContractBeneficiaryService;
    
    @Resource
    private AccountService accountService;
    
    /**
     * 创建合同
     * @param recycleContract 合同信息（包含受益人列表）
     * @return 创建后的合同信息（包含受益人列表）
     */
    @Transactional(rollbackFor = Exception.class)
    public RecycleContract createContract(RecycleContract recycleContract) {
        // 保存合同
        save(recycleContract);
        
        // 保存受益人列表
        List<RecycleContractBeneficiary> beneficiaries = recycleContract.getBeneficiaries();
        if (beneficiaries != null && !beneficiaries.isEmpty()) {
            recycleContractBeneficiaryService.saveOrUpdateBeneficiaries(recycleContract.getId(), beneficiaries);
        }
        
        // 重新查询并设置受益人列表
        beneficiaries = recycleContractBeneficiaryService.getBeneficiariesByContractId(recycleContract.getId());
        recycleContract.setBeneficiaries(beneficiaries);
        
        // 填充受益人名称
        fillBeneficiaryNames(recycleContract);
        
        return recycleContract;
    }

    /**
     * 根据合作方ID查询合同列表
     * @param partnerId 合作方ID
     * @return
     */
    public List<RecycleContract> findByPartner(Long partnerId) {
        return list(new LambdaQueryWrapper<RecycleContract>()
        .eq(RecycleContract::getPartner, partnerId));
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
    
    /**
     * 根据ID查询合同（包含受益人列表）
     * @param id 合同ID
     * @return 合同信息（包含受益人列表）
     */
    public RecycleContract getContractById(String id) {
        RecycleContract contract = super.getById(id);
        if (contract != null) {
            // 查询并设置受益人列表
            List<RecycleContractBeneficiary> beneficiaries = recycleContractBeneficiaryService.getBeneficiariesByContractId(id);
            contract.setBeneficiaries(beneficiaries);
            // 填充受益人名称
            fillBeneficiaryNames(contract);
        }
        return contract;
    }
    
    /**
     * 更新合同信息（包含受益人列表）
     * @param recycleContract 合同信息（包含受益人列表）
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateContract(RecycleContract recycleContract) {
        // 更新合同基本信息
        boolean result = updateById(recycleContract);
        
        // 更新受益人列表
        if (recycleContract.getBeneficiaries() != null) {
            recycleContractBeneficiaryService.saveOrUpdateBeneficiaries(
                recycleContract.getId(), 
                recycleContract.getBeneficiaries()
            );
        }
        
        return result;
    }
    
    /**
     * 根据合同编号查询合同（包含受益人列表）
     * @param no 合同编号
     * @return 合同信息（包含受益人列表）
     */
    public RecycleContract getContractByNo(String no) {
        RecycleContract contract = getOne(new LambdaQueryWrapper<RecycleContract>().eq(RecycleContract::getNo, no));
        if (contract != null) {
            // 查询并设置受益人列表
            List<RecycleContractBeneficiary> beneficiaries = recycleContractBeneficiaryService.getBeneficiariesByContractId(contract.getId());
            contract.setBeneficiaries(beneficiaries);
            // 填充受益人名称
            fillBeneficiaryNames(contract);
        }
        return contract;
    }
    
    /**
     * 查询所有合同（包含受益人列表）
     * @return 合同列表
     */
    public List<RecycleContract> getAllContractsWithBeneficiaries() {
        List<RecycleContract> contracts = list();
        // 为每个合同填充受益人信息和名称
        for (RecycleContract contract : contracts) {
            List<RecycleContractBeneficiary> beneficiaries = recycleContractBeneficiaryService.getBeneficiariesByContractId(contract.getId());
            contract.setBeneficiaries(beneficiaries);
            fillBeneficiaryNames(contract);
        }
        return contracts;
    }
    
    /**
     * 分页查询合同（包含受益人列表）
     * @param page 分页对象
     * @return 分页结果
     */
    public IPage<RecycleContract> getContractPageWithBeneficiaries(Page<RecycleContract> page) {
        IPage<RecycleContract> result = page(page);
        // 为每个合同填充受益人信息和名称
        for (RecycleContract contract : result.getRecords()) {
            List<RecycleContractBeneficiary> beneficiaries = recycleContractBeneficiaryService.getBeneficiariesByContractId(contract.getId());
            contract.setBeneficiaries(beneficiaries);
            fillBeneficiaryNames(contract);
        }
        return result;
    }
    
    /**
     * 填充受益人名称（根据beneficiaryId查询用户表）
     * @param contract 合同对象
     */
    private void fillBeneficiaryNames(RecycleContract contract) {
        if (contract == null || contract.getBeneficiaries() == null || contract.getBeneficiaries().isEmpty()) {
            return;
        }
        
        // 收集所有受益人ID
        List<String> beneficiaryIds = contract.getBeneficiaries().stream()
                .map(RecycleContractBeneficiary::getBeneficiaryId)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .collect(Collectors.toList());
        
        if (beneficiaryIds.isEmpty()) {
            return;
        }
        
        // 批量查询用户信息
        Map<String, Account> userMap = accountService.listByIds(beneficiaryIds).stream()
                .collect(Collectors.toMap(Account::getId, account -> account, (existing, replacement) -> existing));
        
        // 为每个受益人设置名称
        for (RecycleContractBeneficiary beneficiary : contract.getBeneficiaries()) {
            if (StrUtil.isNotBlank(beneficiary.getBeneficiaryId())) {
                Account account = userMap.get(beneficiary.getBeneficiaryId());
                if (account != null) {
                    // 优先使用nickname，如果没有则使用username
                    String name = StrUtil.isNotBlank(account.getNickname()) ? account.getNickname() : account.getUsername();
                    beneficiary.setBeneficiaryName(name);
                }
            }
        }
    }
}