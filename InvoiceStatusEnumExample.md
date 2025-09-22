# 发票状态枚举使用示例

## 枚举定义

### RecycleInvoiceStatusEnum

```java
public enum RecycleInvoiceStatusEnum {
    /**
     * 待开票
     */
    PENDING("pending", "待开票"),
    
    /**
     * 已开票
     */
    INVOICED("invoiced", "已开票");
    
    private final String code;
    private final String description;
}
```

## 接口使用

### 1. 获取所有发票状态

**接口地址**: `GET /recycle/invoice/status/all`

**响应示例**:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": [
        {
            "value": "pending",
            "label": "待开票"
        },
        {
            "value": "invoiced",
            "label": "已开票"
        }
    ]
}
```

### 2. 确认发票接口

**接口地址**: `POST /recycle/invoice/confirm`

**请求示例**:
```json
{
    "invoiceId": "1234567890",
    "processor": "张三",
    "invoiceNo": "INV20240101001",
    "invoiceTime": "2024-01-01 10:00:00",
    "totalAmount": 1000.00,
    "taxAmount": 130.00
}
```

**业务逻辑**:
- 系统会自动将发票状态更新为 `RecycleInvoiceStatusEnum.INVOICED.getCode()` (即 "invoiced")
- 状态描述为 "已开票"

### 3. 创建发票

**业务逻辑**:
- 新创建的发票默认状态为 `RecycleInvoiceStatusEnum.PENDING.getCode()` (即 "pending")
- 状态描述为 "待开票"

## 状态流转

```
创建发票 → 待开票 (pending)
    ↓
确认发票 → 已开票 (invoiced)
```

## 代码中的使用

### 1. 在Service中使用枚举

```java
// 设置状态为已开票
invoice.setStatus(RecycleInvoiceStatusEnum.INVOICED.getCode());

// 设置默认状态为待开票
invoice.setStatus(RecycleInvoiceStatusEnum.PENDING.getCode());
```

### 2. 根据代码获取枚举

```java
RecycleInvoiceStatusEnum status = RecycleInvoiceStatusEnum.getByCode("pending");
if (status != null) {
    System.out.println("状态描述: " + status.getDescription());
}
```

## 优势

1. **类型安全**: 使用枚举避免硬编码字符串，减少拼写错误
2. **统一管理**: 所有状态定义集中在一个地方，便于维护
3. **易于扩展**: 新增状态只需在枚举中添加即可
4. **代码可读性**: 枚举名称和描述清晰表达业务含义
5. **API友好**: 提供获取所有状态的接口，便于前端使用

## 注意事项

1. 状态代码使用小写字母，如 "pending", "invoiced"
2. 状态描述使用中文，如 "待开票", "已开票"
3. 所有状态相关的操作都应该使用枚举，避免直接使用字符串
4. 新增状态时需要考虑数据迁移和兼容性问题
