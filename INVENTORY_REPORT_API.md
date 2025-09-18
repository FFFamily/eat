# 库存统计报表API文档

## 概述

库存统计报表接口用于统计仓储订单中的流转方向，将入库的订单作为库存总表，订单中每有一个不同的货物编号的货物明细就有一条库存总表记录。

## 接口列表

### 1. 获取库存统计报表（分页）

**接口地址：** `GET /inventory/report/list`

**功能描述：** 获取所有货物的库存统计报表（支持分页）

**请求参数：**
- `page`: 页码，默认为1
- `size`: 每页大小，默认为10

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "goodNo": "GOOD001",
        "goodName": "废旧金属",
        "goodType": "金属类",
        "goodModel": "A型",
        "totalInQuantity": 100,
        "totalOutQuantity": 30,
        "currentStock": 70,
        "totalInAmount": 10000.00,
        "totalOutAmount": 3000.00,
        "stockValue": 7000.00,
        "averagePrice": 100.00,
        "lastInTime": "2024-01-15 10:30:00",
        "lastOutTime": "2024-01-20 14:20:00",
        "orderCount": 5,
        "flowDirectionStats": "入库:100件, 出库:30件"
      }
    ],
    "total": 25,
    "size": 10,
    "current": 1,
    "pages": 3
  }
}
```

### 2. 根据货物编号获取库存详情

**接口地址：** `GET /inventory/report/detail/{goodNo}`

**功能描述：** 根据货物编号获取特定货物的库存详情

**路径参数：**
- `goodNo`: 货物编号

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "goodNo": "GOOD001",
    "goodName": "废旧金属",
    "goodType": "金属类",
    "goodModel": "A型",
    "totalInQuantity": 100,
    "totalOutQuantity": 30,
    "currentStock": 70,
    "totalInAmount": 10000.00,
    "totalOutAmount": 3000.00,
    "stockValue": 7000.00,
    "averagePrice": 100.00,
    "lastInTime": "2024-01-15 10:30:00",
    "lastOutTime": "2024-01-20 14:20:00",
    "orderCount": 5,
    "flowDirectionStats": "入库:100件, 出库:30件"
  }
}
```

### 3. 获取库存统计汇总

**接口地址：** `GET /inventory/report/summary`

**功能描述：** 获取库存统计的汇总信息

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalGoodsTypes": 25,
    "totalInQuantity": 1000,
    "totalOutQuantity": 300,
    "totalCurrentStock": 700,
    "totalInAmount": 100000.00,
    "totalOutAmount": 30000.00,
    "totalStockValue": 70000.00
  }
}
```

### 4. 根据货物分类筛选库存报表（分页）

**接口地址：** `GET /inventory/report/filter/type/{goodType}`

**功能描述：** 根据货物分类筛选库存报表（支持分页）

**路径参数：**
- `goodType`: 货物分类

**请求参数：**
- `page`: 页码，默认为1
- `size`: 每页大小，默认为10

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "goodNo": "GOOD001",
        "goodName": "废旧金属",
        "goodType": "金属类",
        "currentStock": 70,
        "flowDirectionStats": "入库:100件, 出库:30件"
      }
    ],
    "total": 5,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 5. 获取库存预警信息（分页）

**接口地址：** `GET /inventory/report/warning`

**功能描述：** 获取库存数量小于指定阈值的货物预警信息（支持分页）

**请求参数：**
- `threshold`: 预警阈值，默认为10
- `page`: 页码，默认为1
- `size`: 每页大小，默认为10

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "goodNo": "GOOD002",
        "goodName": "废旧塑料",
        "goodType": "塑料类",
        "currentStock": 5,
        "flowDirectionStats": "入库:20件, 出库:15件"
      }
    ],
    "total": 3,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 6. 获取库存统计报表（不分页）

**接口地址：** `GET /inventory/report/list/all`

**功能描述：** 获取所有货物的库存统计报表（不分页，返回完整列表）

**请求参数：** 无

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "goodNo": "GOOD001",
      "goodName": "废旧金属",
      "goodType": "金属类",
      "currentStock": 70,
      "flowDirectionStats": "入库:100件, 出库:30件"
    }
  ]
}
```

### 7. 根据货物分类筛选库存报表（不分页）

**接口地址：** `GET /inventory/report/filter/type/{goodType}/all`

**功能描述：** 根据货物分类筛选库存报表（不分页）

**路径参数：**
- `goodType`: 货物分类

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "goodNo": "GOOD001",
      "goodName": "废旧金属",
      "goodType": "金属类",
      "currentStock": 70,
      "flowDirectionStats": "入库:100件, 出库:30件"
    }
  ]
}
```

### 8. 获取库存预警信息（不分页）

**接口地址：** `GET /inventory/report/warning/all`

**功能描述：** 获取库存数量小于指定阈值的货物预警信息（不分页）

**请求参数：**
- `threshold`: 预警阈值，默认为10

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "goodNo": "GOOD002",
      "goodName": "废旧塑料",
      "goodType": "塑料类",
      "currentStock": 5,
      "flowDirectionStats": "入库:20件, 出库:15件"
    }
  ]
}
```

### 9. 导出库存统计报表

**接口地址：** `GET /inventory/report/export`

**功能描述：** 导出库存统计报表（功能待实现）

**响应示例：**
```json
{
  "code": 200,
  "message": "操作成功",
  "data": "导出功能待实现"
}
```

## 分页说明

### 分页响应格式

所有分页接口都返回 `IPage<InventoryReportDto>` 格式，包含以下分页信息：

| 字段名 | 类型 | 说明 |
|--------|------|------|
| records | List<InventoryReportDto> | 当前页的数据列表 |
| total | Long | 总记录数 |
| size | Long | 每页大小 |
| current | Long | 当前页码 |
| pages | Long | 总页数 |

### 分页参数说明

| 参数名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| page | int | 1 | 页码，从1开始 |
| size | int | 10 | 每页大小，建议范围1-100 |

### 分页接口列表

- `GET /inventory/report/list` - 分页获取库存统计报表
- `GET /inventory/report/filter/type/{goodType}` - 分页获取按分类筛选的库存报表
- `GET /inventory/report/warning` - 分页获取库存预警信息

### 非分页接口列表

- `GET /inventory/report/list/all` - 获取所有库存统计报表
- `GET /inventory/report/filter/type/{goodType}/all` - 获取按分类筛选的库存报表
- `GET /inventory/report/warning/all` - 获取所有库存预警信息
- `GET /inventory/report/detail/{goodNo}` - 获取单个货物库存详情
- `GET /inventory/report/summary` - 获取库存统计汇总

## 数据字段说明

### InventoryReportDto 字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| goodNo | String | 货物编号 |
| goodName | String | 货物名称 |
| goodType | String | 货物分类 |
| goodModel | String | 货物型号 |
| totalInQuantity | Integer | 入库总数量 |
| totalOutQuantity | Integer | 出库总数量 |
| currentStock | Integer | 当前库存数量 |
| totalInAmount | BigDecimal | 入库总金额 |
| totalOutAmount | BigDecimal | 出库总金额 |
| stockValue | BigDecimal | 库存总价值 |
| averagePrice | BigDecimal | 平均单价 |
| lastInTime | Date | 最后入库时间 |
| lastOutTime | Date | 最后出库时间 |
| orderCount | Integer | 订单数量（包含该货物的订单总数） |
| flowDirectionStats | String | 流转方向统计 |

## 业务逻辑说明

1. **库存计算逻辑：**
   - 根据回收订单的 `flowDirection` 字段判断入库/出库
   - 入库：`flowDirection` 为 "IN" 或 "入库"
   - 出库：`flowDirection` 为 "OUT" 或 "出库"
   - 当前库存 = 入库总数量 - 出库总数量

2. **货物分组：**
   - 按 `goodNo`（货物编号）进行分组
   - 每个不同的货物编号生成一条库存记录

3. **金额计算：**
   - 平均单价 = 入库总金额 / 入库总数量
   - 库存总价值 = 平均单价 × 当前库存数量

4. **时间统计：**
   - 最后入库时间：该货物最后一次入库订单的创建时间
   - 最后出库时间：该货物最后一次出库订单的创建时间

## 注意事项

1. 该接口基于回收订单（RecycleOrder）和回收订单明细（RecycleOrderItem）表进行统计
2. 只统计已删除标识为0的记录
3. 如果某个货物只有入库没有出库，出库相关字段为0或null
4. 如果某个货物只有出库没有入库，入库相关字段为0或null
5. 平均单价只基于入库记录计算，出库记录不参与平均单价计算