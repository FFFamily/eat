# 库存管理模块（hs_inventory）

## 模块简介

库存管理模块提供了完整的仓库和库存管理功能，包括仓库管理、库存查询、出入库管理、库存流水追溯等核心功能。

## 功能特性

### 1. 仓库管理
- 仓库信息的增删改查
- 仓库状态管理（启用/停用）
- 支持多种仓库类型（原料仓、成品仓、中转仓）
- 仓库负责人管理

### 2. 库存管理
- 实时库存查询
- 可用库存和锁定库存分离管理
- 安全库存预警
- 库存统计报表
- 支持库存锁定和解锁（防止超卖）

### 3. 入库管理
- 创建入库单
- 支持多种入库类型（采购入库、退货入库、调拨入库、其他入库）
- 入库单审核流程
- 入库自动更新库存

### 4. 出库管理
- 创建出库单
- 支持多种出库类型（销售出库、报损出库、调拨出库、其他出库）
- 出库单审核流程
- 出库自动更新库存
- 库存不足检查

### 5. 库存流水
- 完整的库存变动记录
- 支持按仓库、货物、交易类型、业务类型查询
- 变动前后库存快照
- 便于库存追溯和对账

## 数据库表结构

模块包含7张核心数据表：

1. **warehouse** - 仓库表
2. **inventory** - 库存主表
3. **inventory_in** - 入库单表
4. **inventory_in_item** - 入库明细表
5. **inventory_out** - 出库单表
6. **inventory_out_item** - 出库明细表
7. **inventory_transaction** - 库存流水表

执行SQL脚本：`hs_inventory/sql/inventory_tables.sql`

## API接口

### 仓库管理接口 (/inventory/warehouse)

- `POST /create` - 创建仓库
- `PUT /update` - 更新仓库
- `GET /get/{id}` - 查询仓库
- `DELETE /delete/{id}` - 删除仓库
- `PUT /updateStatus` - 启用/停用仓库
- `GET /page` - 分页查询仓库
- `GET /listActive` - 获取所有启用的仓库

### 库存查询接口 (/inventory)

- `GET /get/{id}` - 根据ID查询库存
- `GET /getByWarehouseAndGood` - 根据仓库和货物查询库存
- `POST /page` - 分页查询库存
- `GET /warning` - 获取库存预警列表
- `GET /statistics` - 库存统计
- `PUT /setMinStock` - 设置安全库存
- `PUT /lockStock` - 锁定库存
- `PUT /unlockStock` - 解锁库存

### 入库管理接口 (/inventory/in)

- `POST /create` - 创建入库单
- `GET /get/{id}` - 查询入库单
- `GET /items/{inId}` - 查询入库单明细
- `PUT /confirm/{inId}` - 确认入库
- `PUT /cancel/{inId}` - 取消入库单
- `GET /page` - 分页查询入库单

### 出库管理接口 (/inventory/out)

- `POST /create` - 创建出库单
- `GET /get/{id}` - 查询出库单
- `GET /items/{outId}` - 查询出库单明细
- `PUT /confirm/{outId}` - 确认出库
- `PUT /cancel/{outId}` - 取消出库单
- `GET /page` - 分页查询出库单

### 库存流水接口 (/inventory/transaction)

- `GET /get/{id}` - 查询库存流水
- `GET /page` - 分页查询库存流水

## 使用示例

### 1. 创建仓库

```json
POST /inventory/warehouse/create
{
  "warehouseNo": "WH001",
  "warehouseName": "主仓库",
  "warehouseAddress": "北京市朝阳区xxx",
  "warehouseType": "finished_product",
  "managerId": "manager001",
  "managerName": "张三",
  "contactPhone": "13800138000",
  "status": "active",
  "remark": "主要存放成品"
}
```

### 2. 创建入库单

```json
POST /inventory/in/create
{
  "warehouseId": "1234567890",
  "inType": "purchase",
  "sourceOrderId": "PO001",
  "sourceOrderNo": "PO-20250120-001",
  "remark": "采购入库",
  "items": [
    {
      "goodNo": "G001",
      "goodName": "产品A",
      "goodType": "电子产品",
      "goodModel": "型号A1",
      "inQuantity": "100",
      "unit": "个",
      "remark": ""
    }
  ]
}
```

### 3. 确认入库

```
PUT /inventory/in/confirm/{inId}
```

确认后会自动：
- 更新库存数量
- 记录库存流水
- 检查库存预警

### 4. 创建出库单

```json
POST /inventory/out/create
{
  "warehouseId": "1234567890",
  "outType": "sale",
  "targetOrderId": "SO001",
  "targetOrderNo": "SO-20250120-001",
  "remark": "销售出库",
  "items": [
    {
      "goodNo": "G001",
      "goodName": "产品A",
      "goodType": "电子产品",
      "goodModel": "型号A1",
      "outQuantity": "50",
      "unit": "个",
      "remark": ""
    }
  ]
}
```

### 5. 查询库存预警

```
GET /inventory/warning?warehouseId=1234567890
```

返回所有库存不足的货物列表。

## 业务流程

### 入库流程

1. 创建入库单（状态：待入库）
2. 填写入库明细
3. 确认入库
4. 系统自动增加库存
5. 记录库存流水
6. 触发库存预警检查

### 出库流程

1. 创建出库单（状态：待出库）
2. 填写出库明细
3. 检查库存是否充足
4. 确认出库
5. 系统自动减少库存
6. 记录库存流水
7. 触发库存预警检查

## 技术特点

1. **事务管理**：使用`@Transactional`确保出入库操作的原子性
2. **库存防超卖**：通过可用库存和锁定库存分离管理
3. **完整追溯**：记录详细的库存流水，包括变动前后的库存快照
4. **安全库存预警**：支持设置最小库存，自动预警
5. **MyBatis-Plus**：使用MyBatis-Plus简化数据库操作

## 注意事项

1. 确认入库/出库操作不可逆，请谨慎操作
2. 只有待处理状态的单据可以取消
3. 出库时会检查可用库存，不足时无法出库
4. 建议定期对库存进行盘点和调整
5. 重要操作建议配置权限控制

## 后续优化建议

1. 添加库存盘点功能
2. 添加库存调拨功能
3. 支持批次管理
4. 支持货位管理
5. 添加库存报表和图表展示
6. 集成库存预测和自动补货功能

