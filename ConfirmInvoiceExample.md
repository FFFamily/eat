# 确认发票接口使用示例

## 接口地址
```
POST /recycle/invoice/confirm
```

## 请求参数（ConfirmInvoiceRequest）

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| invoiceId | String | 是 | 发票ID |
| processor | String | 是 | 经办人 |
| invoiceNo | String | 是 | 发票编号 |
| invoiceTime | Date | 是 | 开票时间 |
| totalAmount | BigDecimal | 是 | 总金额 |
| taxAmount | BigDecimal | 是 | 税额 |

## 请求示例

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

## 响应示例

### 成功响应
```json
{
    "code": 200,
    "message": "操作成功",
    "data": true
}
```

### 失败响应
```json
{
    "code": 500,
    "message": "发票确认异常：发票不存在",
    "data": null
}
```

## 业务逻辑说明

1. **参数验证**：系统会验证发票ID是否存在
2. **金额计算**：自动计算不含税金额 = 总金额 - 税额
3. **状态更新**：将发票状态更新为"已开票"
4. **信息更新**：更新经办人、发票编号、开票时间、总金额、税额、不含税金额
5. **时间记录**：自动更新修改时间

## 注意事项

1. 发票ID必须存在，否则会抛出异常
2. 总金额和税额必须为有效的数值
3. 不含税金额会自动计算，无需前端传递
4. 发票状态会固定更新为"已开票"
5. 操作具有事务性，确保数据一致性
