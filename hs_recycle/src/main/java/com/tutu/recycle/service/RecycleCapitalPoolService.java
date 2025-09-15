package com.tutu.recycle.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleCapitalPoolItem;
import com.tutu.recycle.entity.RecycleContract;
import com.tutu.recycle.mapper.RecycleCapitalPoolMapper;

import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Service
public class RecycleCapitalPoolService extends ServiceImpl<RecycleCapitalPoolMapper, RecycleCapitalPool> {
    // 资金池明细服务
    @Resource
    private RecycleCapitalPoolItemService itemService;

    @Resource
    private RecycleContractService recycleContractService;
    
    // 使用ConcurrentHashMap存储资金池编号对应的锁
    private final ConcurrentHashMap<String, ReentrantLock> poolLocks = new ConcurrentHashMap<>();
    

    /**
     * 获取资金池明细
     * @param capitalPoolId 资金池ID
     * @return
     */
    public List<RecycleCapitalPoolItem> getItem(String capitalPoolId) {
        return itemService.list(new LambdaQueryWrapper<RecycleCapitalPoolItem>()
        .eq(RecycleCapitalPoolItem::getCapitalPoolId, capitalPoolId));
    }

    /**
     * 获取资金池锁
     * @param poolNo 资金池编号
     * @return 对应的锁对象
     */
    private ReentrantLock getPoolLock(String poolNo) {
        return poolLocks.computeIfAbsent(poolNo, k -> new ReentrantLock());
    }
    
    /**
     * 尝试获取资金池锁
     * @param key 资金池编号
     * @return 是否成功获取锁
     */
    private boolean tryLock(String key) {
        ReentrantLock lock = getPoolLock(key);
        return lock.tryLock();
    }
    
    /**
     * 释放资金池锁
     * @param poolNo 资金池编号
     */
    private void releaseLock(String poolNo) {
        ReentrantLock lock = poolLocks.get(poolNo);
        if (lock != null && lock.isHeldByCurrentThread()) {
            lock.unlock();
            // 如果锁没有被其他线程持有，则从Map中移除，防止内存泄漏
            if (!lock.isLocked()) {
                poolLocks.remove(poolNo);
            }
        }
    }
    
    /**
     * 清理过期的锁（可选，用于定期清理）
     */
    public void cleanExpiredLocks() {
        poolLocks.entrySet().removeIf(entry -> !entry.getValue().isLocked());
    }

    /**
     * 根据合同编号获取资金池（带合同和账户信息）
     * @param contractNo
     * @return
     */
    public RecycleCapitalPool getByContractNo(String contractNo) {
        return baseMapper.selectByContractNoWithDetails(contractNo);
    }

    /**
     * 根据合同id获取资金池（带合同和账户信息）
     * @param contractId
     * @return
     */
    public RecycleCapitalPool getByContractId(String contractId) {
        return baseMapper.selectByContractIdWithDetails(contractId);
    }
    
    /**
     * 根据ID获取资金池（带合同和账户信息）
     * @param id
     * @return
     */
    public RecycleCapitalPool getByIdWithDetails(String id) {
        return baseMapper.selectByIdWithDetails(id);
    }
    
    /**
     * 分页查询资金池（带合同和账户信息）
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    public Page<RecycleCapitalPool> pageWithDetails(Page<RecycleCapitalPool> page, RecycleCapitalPool query) {
        return baseMapper.selectPageWithDetails(page, query);
    }

    /**
     * 新增资金池
     * @param entity
     */
    public void create(RecycleCapitalPool entity) {
        // 编号唯一
        String no = entity.getNo();
        RecycleCapitalPool one = getOne(new LambdaQueryWrapper<RecycleCapitalPool>().eq(RecycleCapitalPool::getNo, no));
        if (one != null) {
            throw new RuntimeException("编号已存在");
        }
        // 根据合同查询合同id
        RecycleContract contract = recycleContractService.getOne(new LambdaQueryWrapper<RecycleContract>().eq(RecycleContract::getNo, entity.getContractNo()));
        if (contract == null) {
            throw new RuntimeException("合同不存在");
        }
        entity.setContractId(contract.getId());
        // 一个合同只能有一个资金池
        one = getOne(new LambdaQueryWrapper<RecycleCapitalPool>().eq(RecycleCapitalPool::getContractId, contract.getId()));
        if (one != null) {
            throw new RuntimeException("一个合同只能有一个资金池");
        }
        // 计算余额
        entity.setBalance(entity.getInitialAmount());
        // 保存
        this.save(entity);
    }

    /**
     * 删除资金池
     * @param id
     */
    public void deletePool(String id) {
        List<RecycleCapitalPoolItem> items = itemService.getPoolByPoolId(id);
        if (items.size() > 0) {
            throw new RuntimeException("资金池下有明细，不能删除");
        }
        this.removeById(id);
    }

    /**
     * 安全的资金池金额变更方法
     * 使用资金池编号加锁保证并发安全，所有涉及金额变更的操作都应该调用此方法
     * 
     * @param contractId 合同ID
     * @param amount 变更金额（正数表示增加，负数表示减少）
     * @param operationType 操作类型
     * @param orderId 关联订单ID
     * @return 变更后的资金池余额
     */
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal safeUpdateBalance(String contractId, BigDecimal amount, String operationType, String orderId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("变更金额不能为空或零");
        }
        // 先根据合同编号查询资金池，获取资金池编号
        RecycleCapitalPool pool = this.getByContractId(contractId);
        if (pool == null) {
            throw new RuntimeException("合同对应资金池不存在：" + contractId);
        }
        String poolId = pool.getId();
        // 尝试获取资金池锁
        if (!tryLock(poolId)) {
            throw new RuntimeException("当前资金池操作繁忙，请稍后重试");
        }
        try {
            // 重新查询资金池，确保数据是最新的
            pool = this.getByContractId(contractId);
            if (pool == null) {
                throw new RuntimeException("资金池不存在：" + contractId);
            }
            BigDecimal oldBalance = pool.getBalance();
            // 计算新余额
            BigDecimal newBalance = oldBalance.add(amount);
            // 更新余额
            pool.setBalance(newBalance);
            boolean updateResult = this.updateById(pool);
            if (!updateResult) {
                throw new RuntimeException("资金池余额更新失败");
            }
            // 记录资金池明细
            try {
                RecycleCapitalPoolItem item = new RecycleCapitalPoolItem();
                item.setCapitalPoolId(poolId);
                item.setDirection(operationType);
                item.setOrderId(orderId);
                item.setBeforeBalance(oldBalance);
                item.setAmount(amount);
                item.setAfterBalance(newBalance);
                itemService.save(item);
            } catch (Exception e) {
                throw new ServiceException("记录资金池明细失败"+e.getMessage());
            }
            return newBalance;
        } finally {
            // 确保锁被释放
            releaseLock(poolId);
        }
    }
    
    /**
     * 增加资金池余额
     * @param contractNo 合同编号
     * @param amount 增加金额
     * @param operationType 操作类型
     * @param orderId 关联订单ID
     * @return 变更后的余额
     */
    public BigDecimal increaseBalance(String contractNo, BigDecimal amount, String operationType, String orderId) {
        return safeUpdateBalance(contractNo, amount, operationType, orderId);
    }
    
    /**
     * 减少资金池余额
     * @param contractNo 合同编号
     * @param amount 减少金额
     * @param operationType 操作类型
     * @param orderId 关联订单ID
     * @return 变更后的余额
     */
    public BigDecimal decreaseBalance(String contractNo, BigDecimal amount, String operationType, String orderId) {
        return safeUpdateBalance(contractNo, amount.negate(), operationType, orderId);
    }
}

