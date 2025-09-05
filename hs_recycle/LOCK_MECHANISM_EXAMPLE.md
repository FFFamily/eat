# 资金池加锁机制示例

## 概述

新的加锁机制使用资金池编号（no）进行加锁，而不是数据库行锁。这样可以提供更好的性能和用户体验。

## 核心机制

### 1. 加锁流程

```java
// 1. 根据合同编号查询资金池，获取资金池编号
RecycleCapitalPool pool = this.getByContractNo(contractNo);
String poolNo = pool.getNo();

// 2. 尝试获取资金池锁
if (!tryLock(poolNo)) {
    throw new RuntimeException("当前资金池操作繁忙，请稍后重试");
}

try {
    // 3. 执行业务逻辑
    // ... 金额变更操作 ...
} finally {
    // 4. 确保锁被释放
    releaseLock(poolNo);
}
```

### 2. 并发场景示例

假设有两个线程同时操作资金池 "POOL_001"：

**线程A（成功获取锁）：**
```
1. 调用 tryLock("POOL_001") -> 返回 true
2. 执行业务逻辑
3. 调用 releaseLock("POOL_001")
```

**线程B（获取锁失败）：**
```
1. 调用 tryLock("POOL_001") -> 返回 false
2. 抛出异常："当前资金池操作繁忙，请稍后重试"
```

## 使用方式

### 1. 增加余额

```java
try {
    BigDecimal newBalance = recycleCapitalPoolService.increaseBalance(
        "CONTRACT_001",           // 合同编号
        new BigDecimal("1000.00"), // 增加金额
        "收款操作",                // 操作类型
        "ORDER_123"               // 订单ID
    );
    System.out.println("操作成功，新余额：" + newBalance);
} catch (RuntimeException e) {
    if (e.getMessage().contains("当前资金池操作繁忙")) {
        // 处理繁忙情况
        System.out.println("资金池繁忙，请稍后重试");
    } else {
        // 处理其他错误
        System.out.println("操作失败：" + e.getMessage());
    }
}
```

### 2. 减少余额

```java
try {
    BigDecimal newBalance = recycleCapitalPoolService.decreaseBalance(
        "CONTRACT_001",           // 合同编号
        new BigDecimal("500.00"),  // 减少金额
        "付款操作",                // 操作类型
        "ORDER_456"               // 订单ID
    );
    System.out.println("操作成功，新余额：" + newBalance);
} catch (RuntimeException e) {
    if (e.getMessage().contains("当前资金池操作繁忙")) {
        System.out.println("资金池繁忙，请稍后重试");
    } else if (e.getMessage().contains("资金池余额不足")) {
        System.out.println("余额不足：" + e.getMessage());
    } else {
        System.out.println("操作失败：" + e.getMessage());
    }
}
```

## 错误处理

### 1. 资金池繁忙

```java
try {
    // 尝试操作资金池
} catch (RuntimeException e) {
    if (e.getMessage().contains("当前资金池操作繁忙")) {
        // 建议用户稍后重试
        // 可以显示友好的提示信息
        showMessage("当前资金池操作繁忙，请稍后重试");
        
        // 或者延迟重试
        scheduleRetry(operation, 5000); // 5秒后重试
    }
}
```

### 2. 余额不足

```java
try {
    // 尝试减少余额
} catch (RuntimeException e) {
    if (e.getMessage().contains("资金池余额不足")) {
        // 显示余额不足信息
        showMessage("资金池余额不足，无法完成操作");
        
        // 可以显示当前余额
        showCurrentBalance();
    }
}
```

## 性能优化建议

### 1. 控制锁的粒度

```java
// 好的做法：只在必要时加锁
@Transactional
public void processOrder(String contractNo, BigDecimal amount) {
    // 其他业务逻辑...
    
    // 只在金额变更时加锁
    recycleCapitalPoolService.increaseBalance(contractNo, amount, "订单处理", null);
    
    // 其他业务逻辑...
}

// 避免：在锁内执行耗时操作
@Transactional
public void badExample(String contractNo, BigDecimal amount) {
    // 不要在锁内执行耗时操作
    recycleCapitalPoolService.increaseBalance(contractNo, amount, "操作", null);
    
    // 避免在这里执行耗时操作
    // heavyOperation(); // 不要这样做
}
```

### 2. 定期清理过期锁

```java
// 可以配置定时任务清理过期的锁
@Scheduled(fixedRate = 300000) // 每5分钟执行一次
public void cleanupExpiredLocks() {
    recycleCapitalPoolService.cleanExpiredLocks();
}
```

## 总结

新的加锁机制提供了：

1. **更好的性能**：避免了数据库行锁的开销
2. **友好的用户体验**：明确的错误提示，而不是等待超时
3. **内存管理**：自动清理过期的锁，防止内存泄漏
4. **易于调试**：锁的状态更清晰，便于问题排查

使用这套机制时，请确保：

- 所有资金池操作都通过安全方法进行
- 适当处理"操作繁忙"的情况
- 在业务层提供友好的用户提示
- 定期清理过期的锁 