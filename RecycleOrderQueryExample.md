# 回收订单分页查询接口使用示例

## 接口地址
```
POST /recycle/order/page
```

## 请求参数（RecycleOrderQueryRequest）

| 参数名 | 类型 | 必填 | 默认值 | 说明 |
|--------|------|------|--------|------|
| page | Integer | 否 | 1 | 页码 |
| size | Integer | 否 | 10 | 每页数量 |
| type | String | 否 | - | 订单类型 |
| status | String | 否 | - | 订单状态 |
| identifyCode | String | 否 | - | 订单识别码（模糊查询） |
| contractPartnerName | String | 否 | - | 合作方名称（模糊查询） |

## 请求示例

### 1. 查询所有订单（分页）
```json
{
    "page": 1,
    "size": 10
}
```

### 2. 按订单类型查询
```json
{
    "page": 1,
    "size": 10,
    "type": "purchase"
}
```

### 3. 按订单状态查询
```json
{
    "page": 1,
    "size": 10,
    "status": "pending"
}
```

### 4. 按订单识别码模糊查询
```json
{
    "page": 1,
    "size": 10,
    "identifyCode": "RO2024"
}
```

### 5. 按合作方名称模糊查询
```json
{
    "page": 1,
    "size": 10,
    "contractPartnerName": "测试公司"
}
```

### 6. 多条件组合查询
```json
{
    "page": 1,
    "size": 20,
    "type": "purchase",
    "status": "completed",
    "identifyCode": "RO2024",
    "contractPartnerName": "测试公司"
}
```

## 响应示例

```json
{
    "code": 200,
    "message": "操作成功",
    "data": {
        "records": [
            {
                "id": "1234567890",
                "no": "RO20240101001",
                "type": "purchase",
                "status": "pending",
                "identifyCode": "RO20240101001",
                "contractPartnerName": "测试合作方",
                "createTime": "2024-01-01 10:00:00",
                // ... 其他订单字段
            }
        ],
        "total": 100,
        "size": 10,
        "current": 1,
        "pages": 10
    }
}
```

## 订单类型枚举值

- `purchase` - 采购订单
- `transport` - 运输订单
- `process` - 加工订单
- `storage` - 仓储订单
- `sale` - 销售订单
- `other` - 其他

## 注意事项

1. 所有查询参数都是可选的，可以根据需要组合使用
2. `identifyCode` 和 `contractPartnerName` 支持模糊查询
3. `type` 和 `status` 使用精确匹配
4. 查询结果按创建时间倒序排列
5. 分页参数有默认值，可以不传
