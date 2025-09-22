# 发票接口JSON示例

## 1. 添加发票接口

### 接口地址
```
POST /recycle/invoice/create
```

### 请求JSON
```json
{
    "invoice": {
        "invoiceNo": "INV20240101001",
        "invoiceType": "销项",
        "invoiceBank": "中国工商银行",
        "plannedInvoiceTime": "2024-01-15 10:00:00",
        "processor": "张三",
        "totalAmount": 1000.00,
        "taxAmount": 130.00,
        "amountWithoutTax": 870.00
    },
    "details": [
        {
            "orderNo": "RO20240101001",
            "orderTotalAmount": 500.00,
            "orderActualInvoice": 500.00,
            "orderShouldInvoice": 500.00
        },
        {
            "orderNo": "RO20240101002",
            "orderTotalAmount": 500.00,
            "orderActualInvoice": 500.00,
            "orderShouldInvoice": 500.00
        }
    ]
}
```

### 请求字段说明

#### invoice 发票信息
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| invoiceNo | String | 是 | 发票号码 |
| invoiceType | String | 是 | 发票类型（进项/销项） |
| invoiceBank | String | 否 | 开票银行 |
| plannedInvoiceTime | Date | 否 | 计划开票时间 |
| processor | String | 否 | 经办人 |
| totalAmount | BigDecimal | 否 | 总金额（系统自动计算） |
| taxAmount | BigDecimal | 否 | 税额 |
| amountWithoutTax | BigDecimal | 否 | 不含税金额 |

#### details 发票明细列表
| 字段名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| orderNo | String | 是 | 订单编号 |
| orderTotalAmount | BigDecimal | 是 | 订单总金额 |
| orderActualInvoice | BigDecimal | 是 | 订单实开发票 |
| orderShouldInvoice | BigDecimal | 是 | 订单应开发票 |

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

## 2. 分页查询发票列表

### 接口地址
```
GET /recycle/invoice/page?page=1&size=10&invoiceType=销项&status=pending
```

### 请求参数
| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | int | 否 | 1 | 页码 |
| size | int | 否 | 10 | 每页大小 |
| invoiceType | String | 否 | - | 发票类型 |
| status | String | 否 | - | 发票状态 |

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "records": [
            {
                "id": "1234567890",
                "invoiceNo": "INV20240101001",
                "invoiceType": "销项",
                "invoiceBank": "中国工商银行",
                "plannedInvoiceTime": "2024-01-15 10:00:00",
                "status": "pending",
                "processor": "张三",
                "invoiceTime": null,
                "totalAmount": 1000.00,
                "taxAmount": 130.00,
                "amountWithoutTax": 870.00,
                "createTime": "2024-01-01 10:00:00",
                "updateTime": "2024-01-01 10:00:00",
                "createBy": "admin",
                "updateBy": "admin",
                "isDeleted": "0"
            },
            {
                "id": "1234567891",
                "invoiceNo": "INV20240101002",
                "invoiceType": "进项",
                "invoiceBank": "中国建设银行",
                "plannedInvoiceTime": "2024-01-16 14:00:00",
                "status": "invoiced",
                "processor": "李四",
                "invoiceTime": "2024-01-16 14:30:00",
                "totalAmount": 2000.00,
                "taxAmount": 260.00,
                "amountWithoutTax": 1740.00,
                "createTime": "2024-01-02 09:00:00",
                "updateTime": "2024-01-16 14:30:00",
                "createBy": "admin",
                "updateBy": "admin",
                "isDeleted": "0"
            }
        ],
        "total": 2,
        "size": 10,
        "current": 1,
        "pages": 1
    }
}
```

## 3. 获取发票详情（包含明细）

### 接口地址
```
GET /recycle/invoice/get/{invoiceId}
```

### 响应JSON（InvoiceDetailResponse）
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": {
        "invoice": {
            "id": "1234567890",
            "invoiceNo": "INV20240101001",
            "invoiceType": "销项",
            "invoiceBank": "中国工商银行",
            "plannedInvoiceTime": "2024-01-15 10:00:00",
            "status": "pending",
            "processor": "张三",
            "invoiceTime": null,
            "totalAmount": 1000.00,
            "taxAmount": 130.00,
            "amountWithoutTax": 870.00,
            "createTime": "2024-01-01 10:00:00",
            "updateTime": "2024-01-01 10:00:00",
            "createBy": "admin",
            "updateBy": "admin",
            "isDeleted": "0"
        },
        "details": [
            {
                "id": "detail001",
                "invoiceId": "1234567890",
                "orderNo": "RO20240101001",
                "orderTotalAmount": 500.00,
                "orderActualInvoice": 500.00,
                "orderShouldInvoice": 500.00,
                "createTime": "2024-01-01 10:00:00",
                "updateTime": "2024-01-01 10:00:00",
                "createBy": "admin",
                "updateBy": "admin",
                "isDeleted": "0"
            },
            {
                "id": "detail002",
                "invoiceId": "1234567890",
                "orderNo": "RO20240101002",
                "orderTotalAmount": 500.00,
                "orderActualInvoice": 500.00,
                "orderShouldInvoice": 500.00,
                "createTime": "2024-01-01 10:00:00",
                "updateTime": "2024-01-01 10:00:00",
                "createBy": "admin",
                "updateBy": "admin",
                "isDeleted": "0"
            }
        ]
    }
}
```

## 4. 获取发票明细列表

### 接口地址
```
GET /recycle/invoice/details/{invoiceId}
```

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": [
        {
            "id": "detail001",
            "invoiceId": "1234567890",
            "orderNo": "RO20240101001",
            "orderTotalAmount": 500.00,
            "orderActualInvoice": 500.00,
            "orderShouldInvoice": 500.00,
            "createTime": "2024-01-01 10:00:00",
            "updateTime": "2024-01-01 10:00:00",
            "createBy": "admin",
            "updateBy": "admin",
            "isDeleted": "0"
        },
        {
            "id": "detail002",
            "invoiceId": "1234567890",
            "orderNo": "RO20240101002",
            "orderTotalAmount": 500.00,
            "orderActualInvoice": 500.00,
            "orderShouldInvoice": 500.00,
            "createTime": "2024-01-01 10:00:00",
            "updateTime": "2024-01-01 10:00:00",
            "createBy": "admin",
            "updateBy": "admin",
            "isDeleted": "0"
        }
    ]
}
```

## 5. 获取所有发票状态

### 接口地址
```
GET /recycle/invoice/status/all
```

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
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

## 6. 确认发票接口

### 接口地址
```
POST /recycle/invoice/confirm
```

### 请求JSON
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

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

### 业务逻辑说明
1. **更新发票信息**：设置经办人、发票编号、开票时间、金额等信息
2. **计算不含税金额**：自动计算不含税金额 = 总金额 - 税额
3. **更新发票状态**：将发票状态更新为"已开票"
4. **更新相关订单状态**：根据发票明细中的订单编号，将相关订单状态更新为"已开票"
5. **事务保证**：整个操作在一个事务中完成，确保数据一致性

## 7. 更新发票状态

### 接口地址
```
PUT /recycle/invoice/status/{invoiceId}?status=invoiced
```

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

## 8. 设置开票时间

### 接口地址
```
PUT /recycle/invoice/setTime/{invoiceId}?invoiceTime=2024-01-01 10:00:00
```

### 响应JSON
```json
{
    "code": 200,
    "msg": "操作成功",
    "data": true
}
```

## 字段说明

### 发票状态枚举
- `pending` - 待开票
- `invoiced` - 已开票

### 发票类型
- `进项` - 进项发票
- `销项` - 销项发票

### 基础字段（继承自BaseEntity）
- `createTime` - 创建时间
- `updateTime` - 更新时间
- `createBy` - 创建人
- `updateBy` - 更新人
- `isDeleted` - 逻辑删除标识（0-未删除，1-已删除）

### 分页字段
- `records` - 数据列表
- `total` - 总记录数
- `size` - 每页大小
- `current` - 当前页码
- `pages` - 总页数
