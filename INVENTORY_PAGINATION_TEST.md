# 库存统计报表分页功能测试指南

## 概述

本文档说明如何测试库存统计报表的分页功能，确保分页查询能够正确处理仓储类型且入库的订单数据。

## 测试接口

### 1. 分页查询库存报表

**接口地址：** `GET /inventory/report/list`

**请求参数：**
- `page`: 页码，默认为1
- `size`: 每页大小，默认为10

**测试用例：**

#### 测试用例1：基本分页查询
```bash
# 获取第1页，每页10条记录
GET /inventory/report/list?page=1&size=10

# 获取第2页，每页5条记录
GET /inventory/report/list?page=2&size=5

# 获取第1页，每页20条记录
GET /inventory/report/list?page=1&size=20
```

#### 测试用例2：边界值测试
```bash
# 页码为0（应该自动调整为1）
GET /inventory/report/list?page=0&size=10

# 每页大小为0（应该自动调整为10）
GET /inventory/report/list?page=1&size=0

# 每页大小超过100（应该限制为100）
GET /inventory/report/list?page=1&size=200
```

#### 测试用例3：大页码测试
```bash
# 获取最后一页
GET /inventory/report/list?page=999&size=10

# 获取超出范围的页码
GET /inventory/report/list?page=1000&size=10
```

### 2. 获取库存报表总数

**接口地址：** `GET /inventory/report/count`

**测试用例：**
```bash
# 获取总记录数
GET /inventory/report/count
```

## 预期响应格式

### 分页查询响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [
      {
        "warehouseAddress": "仓库A",
        "goodNo": "GOOD001",
        "goodName": "废旧金属",
        "goodType": "金属类",
        "goodModel": "A型",
        "totalInQuantity": 100,
        "totalInAmount": 10000.00,
        "orderCount": 5,
        "averagePrice": 100.00,
        "lastInTime": "2024-01-15 10:30:00"
      }
    ],
    "total": 25,
    "size": 10,
    "current": 1,
    "pages": 3
  }
}
```

### 总数查询响应
```json
{
  "code": 200,
  "message": "操作成功",
  "data": 25
}
```

## 验证要点

### 1. 分页参数验证
- [ ] 页码小于1时自动调整为1
- [ ] 每页大小小于1时自动调整为10
- [ ] 每页大小超过100时限制为100

### 2. 分页数据正确性
- [ ] 返回的记录数不超过请求的每页大小
- [ ] 分页信息（total、size、current、pages）计算正确
- [ ] 数据按仓库地址和货物编号排序

### 3. 数据筛选正确性
- [ ] 只返回仓储类型（type='storage'）的订单
- [ ] 只返回入库方向（flow_direction='IN'）的订单
- [ ] 按货物编号正确分组统计

### 4. 性能验证
- [ ] 分页查询响应时间合理
- [ ] 大数据量下分页性能稳定
- [ ] 数据库分页而非内存分页

## 测试数据准备

为了充分测试分页功能，建议准备以下测试数据：

1. **仓储入库订单**：创建多个type='storage'且flow_direction='IN'的订单
2. **不同货物**：确保有多个不同的good_no
3. **不同仓库**：确保有多个不同的warehouse_address
4. **足够数量**：确保总记录数超过分页大小，以便测试多页查询

## 常见问题排查

### 1. 分页返回空数据
- 检查是否有符合条件的仓储入库订单数据
- 检查数据库连接和查询条件

### 2. 分页信息不正确
- 检查MyBatis-Plus分页插件配置
- 检查SQL查询是否正确

### 3. 性能问题
- 检查数据库索引是否合适
- 检查SQL查询是否优化

## 测试脚本示例

```bash
#!/bin/bash

# 测试基本分页
echo "测试基本分页查询..."
curl -X GET "http://localhost:8080/inventory/report/list?page=1&size=10"

# 测试参数验证
echo "测试参数验证..."
curl -X GET "http://localhost:8080/inventory/report/list?page=0&size=0"

# 测试总数查询
echo "测试总数查询..."
curl -X GET "http://localhost:8080/inventory/report/count"
```
