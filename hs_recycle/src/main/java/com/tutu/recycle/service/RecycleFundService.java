package com.tutu.recycle.service;

import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.entity.RecycleOrder;
import com.tutu.recycle.enums.RecycleFundStatusEnum;
import com.tutu.recycle.enums.RecycleMoneyDirectionEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.mapper.RecycleFundMapper;

import cn.hutool.core.bean.BeanUtil;

import java.math.BigDecimal;

import jakarta.annotation.Resource;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecycleFundService extends ServiceImpl<RecycleFundMapper, RecycleFund> {
    @Resource
    private RecycleOrderService recycleOrderService;
    @Resource
    private RecycleCapitalPoolService recycleCapitalPoolService;
    @Resource
    private RecycleContractService recycleContractService;
    /**
     * 新增走款记录
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void create(RecycleFund entity) {
        // 查询订单
        RecycleOrder order = recycleOrderService.getById(entity.getOrderId());
        if (order == null) {
            throw new RuntimeException("走款记录对应订单不存在：" + entity.getOrderId());
        }
        // 查询合同
        RecycleContract contract = recycleContractService.getById(order.getContractId());
        if (contract == null) {
            throw new RuntimeException("走款记录对应合同不存在：" + order.getContractId());
        }
        if (RecycleOrderTypeEnum.isPayOrderType(order.getType())) {
            entity.setFundFlowDirection(RecycleMoneyDirectionEnum.PAY.getCode());
            entity.setFundPoolDirection(RecycleMoneyDirectionEnum.PAY.getCode());
            entity.setFundDirection(RecycleMoneyDirectionEnum.PAY.getCode());
        }else{
            entity.setFundFlowDirection(RecycleMoneyDirectionEnum.OUT.getCode());
            entity.setFundPoolDirection(RecycleMoneyDirectionEnum.OUT.getCode());
            entity.setFundDirection(RecycleMoneyDirectionEnum.OUT.getCode());
        }
        entity.setStatus(RecycleFundStatusEnum.WAIT.getCode());
        this.save(entity);
    }

    /**
     * 批量新增走款记录
     * @param entityList
     */
    @Transactional(rollbackFor = Exception.class)
    public void createBatch(List<RecycleFund> entityList) {
        for (RecycleFund entity : entityList) {
            this.create(entity);
        }
    }

        /**
     * 确认走款
     * @param entity
     */
    @Transactional(rollbackFor = Exception.class)
    public void confirm(RecycleFund entity) {
        // 查询走款
        RecycleFund fund = this.getById(entity.getId());
        if (fund == null) {
            throw new ServiceException("走款记录不存在：" + entity.getId());
        }
        // 使用安全的金额变更方法更新资金池余额
        BigDecimal amount = fund.getFundPoolAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("走款金额不能为空或零");
        }
        // 更新走款记录状态
        BeanUtil.copyProperties(entity, fund, CopyOptions.create().ignoreNullValue());
        entity.setStatus(RecycleFundStatusEnum.CONFIRM.getCode());
        this.updateById(fund);
        // 查询资金池
        RecycleCapitalPool capitalPool = recycleCapitalPoolService.getByContractId(fund.getContractId());
        if (capitalPool == null) {
            throw new ServiceException("合同对应资金池不存在：" + fund.getContractId());
        }
        if (capitalPool.getFundPoolDirection().equals(fund.getFundPoolDirection())) {
            // 走款方向和资金池方向一致，减少资金池余额 
            recycleCapitalPoolService.decreaseBalance(
                fund.getContractId(), 
                amount, 
                fund.getFundPoolDirection(), 
                fund.getOrderId().toString()
            );
        } else {
            // 走款方向和资金池方向不一致，增加资金池余额
            recycleCapitalPoolService.increaseBalance(
                fund.getContractId(), 
                amount, 
                fund.getFundPoolDirection(), 
                fund.getOrderId().toString()
            );
        }
    }

    /**
     * 分页查询走款记录（带合同信息）
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    public Page<RecycleFund> pageWithContract(Page<RecycleFund> page, RecycleFund query) {
        return baseMapper.selectPageWithContract(page, query);
    }

} 