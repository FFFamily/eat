package com.tutu.system.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.util.EntityFieldUtil;
import com.tutu.common.util.ObjectComparator;
import com.tutu.system.entity.SysContract;
import com.tutu.system.entity.SysContractLog;
import com.tutu.system.enums.ContractOperationTypeEnum;
import com.tutu.system.mapper.SysContractMapper;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysContractService extends ServiceImpl<SysContractMapper, SysContract> {
    @Resource
    private SysContractLogService sysContractLogService;
    /**
     * 更新合同
     * @param sysContract 合同对象
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateContract(SysContract sysContract) {
        SysContract contract = getById(sysContract.getId());
        if (contract == null) {
            throw new ServiceException("合同不存在");
        }
        List<ObjectComparator.Diff> diffs = ObjectComparator.compare(contract, sysContract);
        HashMap<String, String> fieldLabelMap = EntityFieldUtil.getFieldLabelMap(SysContract.class);
        diffs.forEach(diff -> {
            diff.setFieldLabel(fieldLabelMap.get(diff.getField()));
        });
        SysContractLog sysContractLog = new SysContractLog();
        sysContractLog.setContractId(contract.getId());
        sysContractLog.setOperationType(ContractOperationTypeEnum.UPDATE.getCode());
        sysContractLog.setOperatorId(StpUtil.getLoginIdAsString());
        sysContractLog.setOperationTime(new Date());
        sysContractLog.setOperationInfo(JSONUtil.toJsonStr(diffs));
        sysContractLogService.save(sysContractLog);
        BeanUtil.copyProperties(sysContract, contract);
        updateById(contract);
    }
}
