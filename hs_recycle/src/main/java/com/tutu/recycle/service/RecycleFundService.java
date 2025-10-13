package com.tutu.recycle.service;

import cn.hutool.core.bean.copier.CopyOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.entity.RecycleFund;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.enums.RecycleFundStatusEnum;
import com.tutu.recycle.enums.RecycleMoneyDirectionEnum;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;
import com.tutu.recycle.mapper.RecycleFundMapper;
import com.tutu.system.service.MessageService;
import com.tutu.system.utils.MessageUtil;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;

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
    @Resource
    private MessageService messageService;
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
        BigDecimal amount = fund.getFundFlowAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new ServiceException("走款金额不能为空或零");
        }
        // 更新走款记录状态
        BeanUtil.copyProperties(entity, fund, CopyOptions.create().ignoreNullValue());
        entity.setStatus(RecycleFundStatusEnum.CONFIRM.getCode());
        this.updateById(fund);
        // 查询资金池
        RecycleCapitalPool capitalPool = recycleCapitalPoolService.getByContractId(fund.getContractId());
        // 资金走款金额
        BigDecimal fundPoolAmount = fund.getFundPoolAmount();
        if (capitalPool == null && fundPoolAmount != null && fundPoolAmount.compareTo(BigDecimal.ZERO) != 0) {
            throw new ServiceException("合同对应资金池不存在：" + fund.getContractId());
        }
        if (capitalPool != null){
            if (capitalPool.getFundPoolDirection().equals(fund.getFundPoolDirection())) {
                // 走款方向和资金池方向一致，减少资金池余额
                recycleCapitalPoolService.decreaseBalance(
                        fund.getContractId(),
                        amount,
                        RecycleMoneyDirectionEnum.PAY.getCode(),
                        fund.getOrderId().toString()
                );
            } else {
                // 走款方向和资金池方向不一致，增加资金池余额
                recycleCapitalPoolService.increaseBalance(
                        fund.getContractId(),
                        amount,
                        RecycleMoneyDirectionEnum.OUT.getCode(),
                        fund.getOrderId().toString()
                );
            }
        }
        // 给特定的用户发送已结算消息
        String userId = fund.getPartner();
        messageService.sendMessage(MessageUtil.buildFundSettleMessage(userId, fund.getId()));
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

    /**
     * 获取合作方的走款记录列表
     * @param partnerId 合作方ID
     * @param status 走款状态，可为空或"all"表示查询所有状态
     * @return 走款记录列表
     */
    public List<RecycleFund> getPartnerFundList(String partnerId, String status) {
        LambdaQueryWrapper<RecycleFund> wrapper = new LambdaQueryWrapper<RecycleFund>()
                .eq(RecycleFund::getPartner, partnerId)
                .orderByDesc(RecycleFund::getCreateTime);
        
        // 如果状态不为空且不是"all"，则添加状态过滤条件
        if (StrUtil.isNotBlank(status) && !"all".equals(status)) {
            wrapper.eq(RecycleFund::getStatus, status);
        }
        
        return list(wrapper);
    }

} 