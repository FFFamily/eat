# 确认开票时更新订单状态说明

## 功能概述

当确认发票时，系统会自动将发票明细中关联的订单状态更新为"已开票"，确保发票和订单状态的一致性。

## 实现逻辑

### 1. 订单状态枚举更新

在 `RecycleOrderStatusEnum` 中新增了"已开票"状态：

```java
/**
 * 已开票
 */
INVOICED("invoiced", "已开票"),
```

### 2. 确认开票流程

#### 2.1 发票信息更新
- 更新发票基本信息（经办人、发票编号、开票时间等）
- 计算不含税金额
- 更新发票状态为"已开票"

#### 2.2 订单状态更新
- 获取发票明细列表
- 遍历明细中的订单编号
- 根据订单编号查询对应订单
- 将订单状态更新为"已开票"

### 3. 核心代码实现

#### 3.1 RecycleInvoiceService.confirmInvoice()
```java
@Transactional(rollbackFor = Exception.class)
public boolean confirmInvoice(ConfirmInvoiceRequest request) {
    // 1. 更新发票信息
    // ... 发票更新逻辑
    
    // 2. 更新相关订单状态
    updateRelatedOrdersStatus(invoice.getId());
    
    return true;
}
```

#### 3.2 更新相关订单状态方法
```java
private void updateRelatedOrdersStatus(String invoiceId) {
    // 获取发票明细
    List<RecycleInvoiceDetail> details = getInvoiceDetails(invoiceId);
    
    for (RecycleInvoiceDetail detail : details) {
        String orderNo = detail.getOrderNo();
        if (orderNo != null && !orderNo.trim().isEmpty()) {
            // 根据订单编号查询订单
            RecycleOrder order = recycleOrderService.getByOrderNo(orderNo);
            if (order != null) {
                // 更新订单状态为已开票
                order.setStatus(RecycleOrderStatusEnum.INVOICED.getCode());
                order.setUpdateTime(new Date());
                recycleOrderService.updateById(order);
            }
        }
    }
}
```

#### 3.3 根据订单编号查询订单方法
```java
public RecycleOrder getByOrderNo(String orderNo) {
    if (StrUtil.isBlank(orderNo)) {
        return null;
    }
    
    LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
    wrapper.eq(RecycleOrder::getNo, orderNo);
    return getOne(wrapper);
}
```

## 数据流转

```
确认开票请求
    ↓
更新发票信息
    ↓
获取发票明细列表
    ↓
遍历明细中的订单编号
    ↓
根据订单编号查询订单
    ↓
更新订单状态为"已开票"
    ↓
事务提交
```

## 状态流转图

```
订单状态流转：
执行中 → 已上传 → 已开票 → 已结算
                ↑
            确认开票时更新
```

## 事务保证

- 使用 `@Transactional(rollbackFor = Exception.class)` 确保整个操作在一个事务中
- 如果任何一步失败，整个操作都会回滚
- 确保发票和订单状态的一致性

## 错误处理

1. **发票不存在**：抛出运行时异常，事务回滚
2. **发票更新失败**：抛出运行时异常，事务回滚
3. **订单不存在**：跳过该订单，继续处理其他订单
4. **订单编号为空**：跳过该明细，继续处理其他明细

## 使用场景

### 1. 发票确认
- 用户确认发票信息后，系统自动更新相关订单状态
- 确保发票和订单状态保持同步

### 2. 状态查询
- 可以根据订单状态查询已开票的订单
- 便于统计和管理

### 3. 业务流程
- 订单从"已上传"状态流转到"已开票"状态
- 为后续的结算流程做准备

## 注意事项

1. **数据一致性**：确保发票明细中的订单编号正确
2. **性能考虑**：如果发票明细很多，可能影响性能
3. **错误处理**：妥善处理订单不存在的情况
4. **日志记录**：建议添加日志记录订单状态更新过程
5. **权限控制**：确保只有有权限的用户才能确认开票

## 测试建议

1. **正常流程测试**：确认开票后检查订单状态是否正确更新
2. **异常情况测试**：测试订单不存在、订单编号为空等异常情况
3. **事务测试**：测试部分更新失败时是否正确回滚
4. **并发测试**：测试并发确认开票时的数据一致性
