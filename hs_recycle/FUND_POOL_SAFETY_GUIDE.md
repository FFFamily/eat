# 资金池安全金额变更使用指南

## 概述

为了确保资金池金额变更的并发安全，我们设计了一套安全的金额变更机制。所有涉及资金池余额变更的操作都应该使用这些方法，而不是直接操作资金池实体。

## 核心方法

### 1. safeUpdateBalance - 核心安全方法

```java
/**
 * 安全的资金池金额变更方法
 * 使用数据库行锁保证并发安全，所有涉及金额变更的操作都应该调用此方法
 * 
 * @param contractNo 合同编号
 * @param amount 变更金额（正数表示增加，负数表示减少）
 * @param operationType 操作类型（用于记录日志）
 * @param orderId 关联订单ID（可选）
 * @return 变更后的资金池余额
 */
@Transactional(rollbackFor = Exception.class)
public BigDecimal safeUpdateBalance(String contractNo, BigDecimal amount, String operationType, String orderId)
```

### 2. 便捷方法

```java
// 增加资金池余额
public BigDecimal increaseBalance(String contractNo, BigDecimal amount, String operationType, String orderId)

// 减少资金池余额
public BigDecimal decreaseBalance(String contractNo, BigDecimal amount, String operationType, String orderId)
```

## 使用场景

### 1. 确认走款时

```java
@Transactional(rollbackFor = Exception.class)
public void confirm(RecycleFund entity) {
    // 更新走款记录状态
    this.updateById(entity);
    
    // 使用安全的金额变更方法更新资金池余额
    BigDecimal amount = entity.getFundAmount();
    if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
        throw new RuntimeException("走款金额不能为空或零");
    }
    
    // 根据走款方向决定是增加还是减少资金池余额
    if (RecycleMoneyDirectionEnum.PAY.getCode().equals(entity.getFundPoolDirection())) {
        // 付款操作，减少资金池余额
        recycleCapitalPoolService.decreaseBalance(
            entity.getContractNo(), 
            amount, 
            "确认走款-付款", 
            entity.getOrderId().toString()
        );
    } else {
        // 收款操作，增加资金池余额
        recycleCapitalPoolService.increaseBalance(
            entity.getContractNo(), 
            amount, 
            "确认走款-收款", 
            entity.getOrderId().toString()
        );
    }
}
```

### 2. 其他金额变更场景

```java
// 手动调整资金池余额
recycleCapitalPoolService.increaseBalance(
    contractNo, 
    new BigDecimal("1000.00"), 
    "手动调整-增加", 
    null
);

// 扣除费用
recycleCapitalPoolService.decreaseBalance(
    contractNo, 
    new BigDecimal("50.00"), 
    "扣除手续费", 
    orderId
);
```

## 安全特性

### 1. 应用层锁机制
- 使用资金池编号（no）进行加锁，保证并发安全
- 防止多个线程同时操作同一资金池
- 其他线程尝试操作时会收到"当前资金池操作繁忙"的错误提示

### 2. 事务管理
- 所有金额变更操作都在事务中执行
- 异常时自动回滚，保证数据一致性

### 3. 余额检查
- 减少操作前检查余额是否足够
- 防止资金池余额变为负数

### 4. 操作记录
- 自动记录所有金额变更明细
- 支持审计和问题追踪

## 注意事项

### 1. 必须使用安全方法
- ❌ 不要直接调用 `updateById()` 修改资金池余额
- ✅ 必须使用 `increaseBalance()` 或 `decreaseBalance()` 方法

### 2. 事务边界
- 金额变更操作必须在事务中执行
- 建议在业务方法上添加 `@Transactional` 注解

### 3. 异常处理
- 金额不足时会抛出异常，需要适当处理
- 建议在业务层捕获并转换为业务异常

### 4. 性能考虑
- 应用层锁只在必要时加锁，性能更好
- 避免在锁内执行耗时操作
- 支持定期清理过期锁，防止内存泄漏

## 错误示例

```java
// ❌ 错误：直接修改资金池余额
public void wrongWay(RecycleCapitalPool pool, BigDecimal amount) {
    pool.setBalance(pool.getBalance().add(amount));
    recycleCapitalPoolService.updateById(pool); // 存在并发安全问题
}

// ❌ 错误：没有事务保护
public void wrongWay2(String contractNo, BigDecimal amount) {
    // 没有 @Transactional 注解，可能导致数据不一致
    recycleCapitalPoolService.increaseBalance(contractNo, amount, "操作", null);
}
```

## 正确示例

```java
// ✅ 正确：使用安全方法
@Transactional(rollbackFor = Exception.class)
public void correctWay(String contractNo, BigDecimal amount, String orderId) {
    try {
        recycleCapitalPoolService.increaseBalance(
            contractNo, 
            amount, 
            "业务操作", 
            orderId
        );
    } catch (RuntimeException e) {
        // 处理业务异常
        throw new BusinessException("资金池操作失败：" + e.getMessage());
    }
}
```

## 总结

通过使用这套安全的金额变更机制，我们可以：

1. **保证并发安全**：使用数据库行锁防止并发问题
2. **确保数据一致性**：事务管理和异常回滚
3. **提供审计支持**：自动记录所有变更明细
4. **简化业务代码**：提供便捷的API接口

请确保所有涉及资金池金额变更的代码都使用这些安全方法！ 