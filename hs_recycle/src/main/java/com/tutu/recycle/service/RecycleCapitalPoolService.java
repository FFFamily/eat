package com.tutu.recycle.service;

import java.math.BigDecimal;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.recycle.entity.RecycleCapitalPool;
import com.tutu.recycle.entity.RecycleCapitalPoolItem;
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
    
    @Resource
    private RecycleCapitalPoolItemService itemService;
    
    // 使用ConcurrentHashMap存储资金池编号对应的锁
    private final ConcurrentHashMap<String, ReentrantLock> poolLocks = new ConcurrentHashMap<>();
    
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
     * @param poolNo 资金池编号
     * @return 是否成功获取锁
     */
    private boolean tryLock(String poolNo) {
        ReentrantLock lock = getPoolLock(poolNo);
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
     * 根据合同编号获取资金池
     * @param contractNo
     * @return
     */
    public RecycleCapitalPool getByContractNo(String contractNo) {
        return getOne(new LambdaQueryWrapper<RecycleCapitalPool>().eq(RecycleCapitalPool::getContractNo, contractNo));
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
        // 一个合同只能有一个资金池
        one = getOne(new LambdaQueryWrapper<RecycleCapitalPool>().eq(RecycleCapitalPool::getContractNo, entity.getContractNo()));
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
     * @param contractNo 合同编号
     * @param amount 变更金额（正数表示增加，负数表示减少）
     * @param operationType 操作类型（用于记录日志）
     * @param orderId 关联订单ID（可选）
     * @return 变更后的资金池余额
     */
    @Transactional(rollbackFor = Exception.class)
    public BigDecimal safeUpdateBalance(String contractNo, BigDecimal amount, String operationType, String orderId) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            throw new RuntimeException("变更金额不能为空或零");
        }
        
        // 先根据合同编号查询资金池，获取资金池编号
        RecycleCapitalPool pool = this.getByContractNo(contractNo);
        if (pool == null) {
            throw new RuntimeException("资金池不存在：" + contractNo);
        }
        
        String poolNo = pool.getNo();
        if (poolNo == null || poolNo.trim().isEmpty()) {
            throw new RuntimeException("资金池编号不能为空");
        }
        
        // 尝试获取资金池锁
        if (!tryLock(poolNo)) {
            throw new RuntimeException("当前资金池操作繁忙，请稍后重试");
        }
        
        try {
            // 重新查询资金池，确保数据是最新的
            pool = this.getByContractNo(contractNo);
            if (pool == null) {
                throw new RuntimeException("资金池不存在：" + contractNo);
            }
            
            // 计算新余额
            BigDecimal newBalance = pool.getBalance().add(amount);
            
            // 检查余额是否足够（如果是减少操作）
            if (amount.compareTo(BigDecimal.ZERO) < 0 && newBalance.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("资金池余额不足，当前余额：" + pool.getBalance() + "，需要扣除：" + amount.abs());
            }
            
            // 更新余额
            pool.setBalance(newBalance);
            boolean updateResult = this.updateById(pool);
            if (!updateResult) {
                throw new RuntimeException("资金池余额更新失败");
            }
            
            // 记录资金池明细（可选，用于审计）
            try {
                recordBalanceChange(pool.getId(), amount, operationType, orderId, pool.getBalance());
            } catch (Exception e) {
                // 记录明细失败不影响主流程，只记录日志
                log.warn("记录资金池明细失败：{}", e.getMessage());
            }
            
            return newBalance;
        } finally {
            // 确保锁被释放
            releaseLock(poolNo);
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
    
    /**
     * 记录资金池余额变更明细
     * @param poolId 资金池ID
     * @param amount 变更金额
     * @param operationType 操作类型
     * @param orderId 关联订单ID
     * @param newBalance 变更后余额
     */
    private void recordBalanceChange(String poolId, BigDecimal amount, String operationType, String orderId, BigDecimal newBalance) {
        // 这里可以调用明细服务记录变更历史
        // 如果RecycleCapitalPoolItemService有相关方法的话
        if (itemService != null) {
            try {
                RecycleCapitalPoolItem item = new RecycleCapitalPoolItem();
                item.setCapitalPoolId(poolId);
                item.setAmount(amount);
                item.setDirection(operationType);
                item.setOrderId(orderId);
                item.setAfterBalance(newBalance);
                item.setCreateTime(new java.util.Date());
                itemService.save(item);
            } catch (Exception e) {
                log.warn("保存资金池明细失败：{}", e.getMessage());
            }
        }
    }
}

