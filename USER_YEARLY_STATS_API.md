# 用户年度统计汇总API文档

## 接口概述
该接口用于获取用户的年度统计汇总信息，包括各种订单类型的收益统计、订单数量统计以及新增的采购相关统计。

## 接口地址
```
GET /wx/data/yearly-stats
```

## 请求参数
| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| year | Integer | 否 | 统计年份，默认为当前年份 |

## 响应数据
```json
{
  "code": 200,
  "msg": "成功",
  "data": {
    "userId": "用户ID",
    "year": 2024,
    "settlementIncome": 100000.00,
    "transportIncome": 50000.00,
    "processingIncome": 30000.00,
    "storageIncome": 20000.00,
    "salesIncome": 80000.00,
    "otherIncome": 10000.00,
    "totalIncome": 290000.00,
    "purchaseOrderCount": 15,
    "transportOrderCount": 8,
    "processingOrderCount": 12,
    "storageOrderCount": 6,
    "salesOrderCount": 10,
    "otherOrderCount": 3,
    "totalOrderCount": 54,
    "purchaseContractCount": 5,
    "allPurchaseOrderCount": 20,
    "settledPurchaseOrderWeight": 1500.50,
    "generateTime": "2024-01-15 10:30:00"
  }
}
```

## 字段说明

### 收益统计
- `settlementIncome`: 结算收益 - 收回订单中类型为采购订单的状态为已结算订单的订单总金额之和
- `transportIncome`: 运输收益
- `processingIncome`: 加工收益
- `storageIncome`: 仓储收益
- `salesIncome`: 销售收益
- `otherIncome`: 其他收益
- `totalIncome`: 总收益

### 订单数量统计
- `purchaseOrderCount`: 采购订单数量（已结算状态）
- `transportOrderCount`: 运输订单数量
- `processingOrderCount`: 加工订单数量
- `storageOrderCount`: 仓储订单数量
- `salesOrderCount`: 销售订单数量
- `otherOrderCount`: 其他订单数量
- `totalOrderCount`: 总订单数量

### 新增采购相关统计
- `purchaseContractCount`: 采购合同数量
- `allPurchaseOrderCount`: 采购订单数量（所有状态的采购订单）
- `settledPurchaseOrderWeight`: 采购订单已结算订单的合计重量

## 使用示例

### 获取当前年度统计
```bash
curl -X GET "http://localhost:8080/wx/data/yearly-stats" \
  -H "Authorization: Bearer your_token_here"
```

### 获取指定年度统计
```bash
curl -X GET "http://localhost:8080/wx/data/yearly-stats?year=2023" \
  -H "Authorization: Bearer your_token_here"
```

## 注意事项
1. 接口需要用户登录认证
2. 统计基于订单的创建时间进行年度筛选
3. 重量统计基于订单明细中的货物重量字段
4. 合同类型为"purchase"的合同被识别为采购合同
5. 已结算状态对应订单状态枚举中的"completed"状态