# 用户订单模块使用说明

## 📋 模块概述

用户订单（UserOrder）模块提供了完整的订单管理功能，包括订单的创建、查询、更新、删除以及订单状态和阶段的流转控制。

## 📁 文件结构

### 1. 实体类
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/entity/user/UserOrder.java`
- **说明**: 用户订单实体类，继承 BaseEntity

### 2. 枚举类

#### UserOrderStageEnum - 订单阶段枚举
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/enums/UserOrderStageEnum.java`
- **阶段定义**:
  - `PURCHASE` (purchase, "采购")
  - `TRANSPORT` (transport, "运输")
  - `PROCESSING` (processing, "加工")
  - `WAREHOUSING` (warehousing, "入库")

#### UserOrderStatusEnum - 订单状态枚举
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/enums/UserOrderStatusEnum.java`
- **状态定义**:
  - `WAIT_TRANSPORT` (wait_transport, "待运输")
  - `WAIT_SORTING` (wait_sorting, "待分拣")
  - `WAIT_WAREHOUSING` (wait_warehousing, "待入库")
  - `COMPLETED` (completed, "已完成")

### 3. 工具类

#### UserOrderNoGenerator - 订单编号生成器
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/utils/UserOrderNoGenerator.java`
- **功能**:
  - `generate()`: 生成订单编号（格式：UO + yyyyMMdd + 4位序号）
  - `generateWithRandom()`: 生成带随机数的订单编号
  - `generateWithTimestamp()`: 生成带时间戳的订单编号
  - `validate()`: 验证订单编号格式
  - `extractDate()`: 从订单编号中提取日期

### 4. Mapper 层
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/mapper/UserOrderMapper.java`
- **说明**: 继承 BaseMapper，提供基础 CRUD 功能

### 5. Service 层

#### UserOrderService
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/service/UserOrderService.java`
- **依赖**: 
  - 注入 `ProcessorService` 用于查询经办人信息
  - 注入 `RecycleOrderService` 用于创建回收订单
- **核心方法**:
  - `createUserOrder()`: 创建用户订单（自动生成订单编号，调用 RecycleOrderService 创建回收订单）
  - `getUserOrderById()`: 根据ID查询订单（自动填充经办人名称）
  - `updateUserOrder()`: 更新订单
  - `deleteUserOrderById()`: 删除订单
  - `getUserOrderByNo()`: 根据订单编号查询（自动填充经办人名称）
  - `getUserOrdersPage()`: 分页查询（支持多条件，自动批量填充经办人名称）
  - `toNextStage()`: 流转到下一个阶段
  - `toNextStatus()`: 流转到下一个状态
  - `completeOrder()`: 完成订单
  - `canTransitionStatus()`: 验证状态流转是否合法
  - `fillProcessorName()`: 填充单个订单的经办人名称（私有方法）
  - `fillProcessorNames()`: 批量填充订单列表的经办人名称（私有方法）

#### RecycleOrderService
- **位置**: `hs_recycle/src/main/java/com/tutu/recycle/service/RecycleOrderService.java`
- **依赖**:
  - 注入 `ProcessorService` 用于查询经办人信息
  - 注入 `RecycleOrderItemService` 用于查询订单明细
  - 注入 `RecycleOrderTraceService` 用于查询订单追溯信息
- **相关方法**:
  - `createPurchaseOrderFromUserOrder()`: 根据用户订单创建采购类型的回收订单（设置parentId关联）
  - `getByParentId()`: 根据父订单ID查询回收订单（返回 RecycleOrderInfo，包含订单明细和追溯信息，确保唯一性，多条数据会抛出异常）

### 6. Controller 层

#### UserOrderController
- **位置**: `hs_api/src/main/java/com/tutu/api/controller/recycle/UserOrderController.java`
- **接口路径**: `/recycle/user/order`

#### RecycleOrderController
- **位置**: `hs_api/src/main/java/com/tutu/api/controller/RecycleOrderController.java`
- **接口路径**: `/recycle/order`
- **新增接口**:
  - `GET /recycle/order/parent/{parentId}`: 根据父订单ID查询回收订单（包含订单明细和追溯信息）

### 7. 数据库表
- **SQL文件**: `sql/user_order_table.sql`
- **表名**: `user_order`

## 🔄 订单流程

### 阶段流程
```
采购(PURCHASE) → 运输(TRANSPORT) → 加工(PROCESSING) → 入库(WAREHOUSING)
```

### 状态流程
```
待运输(WAIT_TRANSPORT) → 待分拣(WAIT_SORTING) → 待入库(WAIT_WAREHOUSING) → 已完成(COMPLETED)
```

### 自动规则
1. **订单编号**: 创建订单时自动生成，格式为 `UO + yyyyMMdd + 序号`（如：UO202401010001）
2. **初始阶段**: 新订单默认为"采购"阶段
3. **初始状态**: 新订单默认为"待运输"状态
4. **阶段和状态**: 不可手动编辑，只能通过流转接口进行变更

## 🌐 API 接口列表

### 基础 CRUD 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/user/order/create` | 创建用户订单 |
| GET | `/user/order/{id}` | 根据ID查询订单 |
| PUT | `/user/order/update` | 更新用户订单 |
| DELETE | `/user/order/delete/{id}` | 根据ID删除订单 |
| GET | `/user/order/list` | 查询所有订单列表 |
| DELETE | `/user/order/batchDelete` | 批量删除订单 |

### 查询接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/user/order/no/{no}` | 根据订单编号查询 |
| GET | `/user/order/contract/{contractId}` | 根据合同ID查询订单列表 |
| GET | `/user/order/contractNo/{contractNo}` | 根据合同编号查询订单列表 |
| GET | `/user/order/status/{status}` | 根据状态查询订单列表 |
| GET | `/user/order/stage/{stage}` | 根据阶段查询订单列表 |
| GET | `/user/order/processor/{processorId}` | 根据经办人ID查询订单列表 |
| GET | `/user/order/page` | 分页查询（支持多条件） |

### 状态流转接口

| 方法 | 路径 | 说明 |
|------|------|------|
| PUT | `/user/order/updateStatus` | 更新订单状态 |
| PUT | `/user/order/updateStage` | 更新订单阶段 |
| PUT | `/user/order/toNextStage` | 流转到下一个阶段 |
| PUT | `/user/order/toNextStatus` | 流转到下一个状态 |
| PUT | `/user/order/complete` | 完成订单 |
| GET | `/user/order/canTransition` | 验证状态流转是否合法 |

## 📝 使用示例

### 1. 创建订单

```json
POST /recycle/user/order/create

{
  "contractId": "RC001",
  "contractNo": "RC-2024-001",
  "contractName": "废金属回收合同2024-001",
  "contractPartner": "PARTNER001",
  "contractPartnerName": "ABC废料回收公司",
  "partyA": "PA001",
  "partyAName": "甲方公司A",
  "partyB": "PB001",
  "partyBName": "乙方公司B",
  "imgUrl": "https://example.com/order/img001.jpg",
  "location": "北京市朝阳区xxx街道",
  "processorId": "PROC001"
}
```

**说明**: 
- 订单编号(`no`)会自动生成，无需传入
- 阶段(`stage`)会自动设置为 `purchase`（采购）
- 状态(`status`)会自动设置为 `wait_transport`（待运输）
- **同步创建回收订单**：会自动创建一个采购类型(`PURCHASE`)的回收订单(`RecycleOrder`)
  - 回收订单状态为 `processing`（执行中）
  - 回收订单的 `parentId` 会设置为用户订单的 ID，建立关联关系
  - 回收订单会复制用户订单的合同信息、甲乙方信息、经办人信息等
  - 回收订单编号使用 UUID 自动生成
  - 两个订单在同一事务中创建，保证数据一致性

### 2. 分页查询订单

```
GET /recycle/user/order/page?current=1&size=10&status=wait_transport&stage=purchase
```

**说明**: 
- 返回的订单列表中会自动填充经办人名称（processorName）
- 使用批量查询优化性能，避免N+1查询问题

### 3. 查询关联的回收订单（包含订单明细）

根据用户订单ID查询对应的回收订单，返回完整的订单信息（包含订单明细和追溯信息）：

```
GET /recycle/order/parent/{userOrderId}
```

**响应示例**：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "RO123456",
    "parentId": "UO202401010001",
    "no": "a1b2c3d4-e5f6...",
    "type": "purchase",
    "status": "processing",
    "contractId": "RC001",
    "contractNo": "RC-2024-001",
    "contractName": "废金属回收合同2024-001",
    "processor": "张三",
    "processorPhone": "13800138000",
    "items": [
      {
        "id": "ITEM001",
        "recycleOrderId": "RO123456",
        "direction": "in",
        "goodNo": "G001",
        "goodType": "废金属",
        "goodName": "废铜",
        "goodModel": "纯铜",
        "goodCount": 100,
        "goodWeight": 500.5,
        "contractPrice": 50.00,
        "goodPrice": 48.00,
        "goodTotalPrice": 4800.00,
        "ratingCoefficient": 0.96,
        "ratingAdjustAmount": -200.00,
        "otherAdjustAmount": 0.00,
        "goodRemark": "成色良好"
      }
    ],
    "sourceCodes": [
      {
        "identifyCode": "SOURCE001",
        "orderId": "ORDER001",
        "orderNo": "RO20240101001",
        "orderType": "purchase",
        "changeReason": "采购入库"
      }
    ]
  }
}
```

**说明**：
- 同一个 parentId 下只会有一个回收订单
- 如果查询到多个订单，会返回错误：`"查询到多个匹配订单，请联系管理员修正数据"`
- 如果未找到订单，会返回：`"未找到对应的回收订单"`
- 返回的 `items` 字段包含订单明细列表，每个明细包含货物的详细信息
- 返回的 `sourceCodes` 字段包含订单的追溯信息

### 4. 订单阶段流转

```
PUT /recycle/user/order/toNextStage?id=UO001
```

**说明**: 订单阶段会自动从当前阶段流转到下一个阶段

### 5. 订单状态流转

```
PUT /recycle/user/order/toNextStatus?id=UO001
```

**说明**: 订单状态会自动从当前状态流转到下一个状态

### 6. 完成订单

```
PUT /recycle/user/order/complete?id=UO001
```

**说明**: 订单状态会被设置为"已完成"，阶段会被设置为"入库"

## 🎯 枚举使用示例

### 在 Java 代码中使用枚举

```java
// 获取枚举值
UserOrderStageEnum stage = UserOrderStageEnum.PURCHASE;
String code = stage.getCode(); // "purchase"
String description = stage.getDescription(); // "采购"

// 根据代码获取枚举
UserOrderStageEnum stage = UserOrderStageEnum.getByCode("purchase");

// 验证枚举值是否有效
boolean isValid = UserOrderStageEnum.isValid("purchase"); // true

// 获取下一个阶段
UserOrderStageEnum nextStage = stage.getNextStage(); // TRANSPORT

// 判断是否是最后一个阶段
boolean isLast = stage.isLastStage(); // false

// 状态流转验证
UserOrderStatusEnum current = UserOrderStatusEnum.WAIT_TRANSPORT;
UserOrderStatusEnum target = UserOrderStatusEnum.WAIT_SORTING;
boolean canTransition = current.canTransitionTo(target); // true
```

## 🔍 订单编号生成器使用示例

```java
// 生成订单编号（序号模式）
String orderNo = UserOrderNoGenerator.generate();
// 结果: UO202401010001

// 生成订单编号（随机数模式）
String orderNo = UserOrderNoGenerator.generateWithRandom();
// 结果: UO20240101123456

// 生成订单编号（时间戳模式）
String orderNo = UserOrderNoGenerator.generateWithTimestamp();
// 结果: UO20240101120000123

// 验证订单编号格式
boolean isValid = UserOrderNoGenerator.validate("UO202401010001"); // true

// 提取日期
String date = UserOrderNoGenerator.extractDate("UO202401010001"); 
// 结果: "20240101"
```

## ⚠️ 注意事项

1. **订单编号**: 由系统自动生成，不可手动指定或修改
2. **阶段和状态**: 通过枚举控制，不可直接设置非法值
3. **流转控制**: 状态和阶段必须按照定义的流程流转，不可跳过
4. **完成订单**: 订单完成后不可再进行状态流转
5. **事务控制**: 所有修改操作都包含事务控制，出错会自动回滚
6. **唯一性**: 建议在数据库中为订单编号(`no`)字段添加唯一索引
7. **经办人名称**: 所有查询接口都会自动填充经办人名称，无需手动关联查询
8. **性能优化**: 列表查询使用批量查询经办人信息，避免循环查询导致的性能问题
9. **同步创建回收订单**: 创建用户订单时会自动创建一个采购类型的回收订单，两者在同一事务中完成
10. **数据一致性**: 用户订单和回收订单共享合同信息、甲乙方信息、经办人信息等，确保数据一致
11. **订单关联**: 回收订单的 parentId 字段存储用户订单的 ID，建立一对一关联关系
12. **唯一性保证**: 通过 getByParentId 方法查询时会验证唯一性，发现多条数据会抛出异常
13. **订单明细**: 通过 getByParentId 查询回收订单时，会自动加载订单明细(items)和追溯信息(sourceCodes)
14. **返回类型**: getByParentId 方法返回 RecycleOrderInfo 对象，包含完整的订单信息、明细列表和追溯信息
15. **明细内容**: 订单明细包含货物的详细信息，如货物编号、名称、型号、数量、重量、价格等
16. **追溯信息**: 订单追溯信息包含订单的来源、识别码、父订单信息、变更原因等

## 🛠 数据库部署

### 1. 创建用户订单表

执行以下 SQL 文件创建用户订单表：

```bash
mysql -u username -p database_name < sql/user_order_table.sql
```

该 SQL 文件包含：
- 表结构定义
- 索引创建
- 示例数据
- 完整的说明文档

### 2. 添加回收订单的 parent_id 字段

如果回收订单表已存在，需要添加 parent_id 字段：

```bash
mysql -u username -p database_name < sql/add_parent_id_to_recycle_order.sql
```

该 SQL 文件包含：
- 添加 parent_id 字段
- 添加 parent_id 索引
- 字段说明和使用文档
- 可选的唯一索引（需要先清理重复数据）

## 📊 数据库索引说明

为了优化查询性能，表中创建了以下索引：

### 单列索引
- `idx_no`: 订单编号
- `idx_status`: 订单状态
- `idx_stage`: 订单阶段
- `idx_contract_id`: 合同ID
- `idx_contract_no`: 合同编号
- `idx_processor_id`: 经办人ID
- `idx_create_time`: 创建时间

### 复合索引
- `idx_user_order_status_stage`: 状态+阶段
- `idx_user_order_contract_composite`: 合同ID+状态
- `idx_user_order_contract_no_status`: 合同编号+状态
- `idx_user_order_processor_composite`: 经办人ID+状态+阶段
- `idx_user_order_time_status`: 创建时间+状态
- `idx_user_order_partner_composite`: 合作方+状态

## 🏗️ 架构设计

### 服务分层
```
UserOrderService (用户订单服务)
    ↓ 调用
RecycleOrderService.createPurchaseOrderFromUserOrder() (创建采购回收订单)
    ├─ 设置 parentId 建立关联
    └─ 查询 ProcessorService (经办人服务)

RecycleOrderController (回收订单控制器)
    ↓ 调用
RecycleOrderService.getByParentId() (查询回收订单 + 明细)
    ├─ 验证唯一性，多条数据抛异常
    ├─ 查询 RecycleOrderItemService (订单明细服务)
    └─ 查询 RecycleOrderTraceService (订单追溯服务)
```

### 订单关联关系
```
UserOrder (用户订单)
    id: "UO202401010001"
        ↓ 1:1 关联
RecycleOrder (回收订单)
    id: "RO123456"
    parentId: "UO202401010001"  ← 关联字段
    type: "purchase"
```

### 设计优势
1. **职责分离**: 创建回收订单的逻辑封装在 RecycleOrderService 中，符合单一职责原则
2. **代码复用**: RecycleOrderService 中的方法可以被其他服务复用
3. **易于维护**: 创建回收订单的逻辑集中管理，便于维护和扩展
4. **事务一致性**: UserOrderService 的 `@Transactional` 注解会传播到 RecycleOrderService，保证事务一致性
5. **关联查询**: 通过 parentId 可以快速查找用户订单对应的回收订单
6. **数据完整性**: 通过唯一性校验确保一个用户订单只对应一个回收订单

## 🔧 后续扩展建议

1. **订单项**: 如需要订单明细，可创建 `user_order_item` 表
2. **订单日志**: 可创建订单变更日志表，记录状态流转历史
3. **订单审批**: 可添加订单审批流程
4. **订单统计**: 可添加订单统计报表功能
5. **消息通知**: 可在订单状态变更时发送消息通知
6. **订单关联**: 可以在 UserOrder 中添加 recycleOrderId 字段，记录关联的回收订单ID
7. **状态同步**: 可以实现用户订单和回收订单之间的状态同步机制
8. **其他订单类型**: 可以在 RecycleOrderService 中添加更多方法，创建其他类型的回收订单（运输、加工、入库等）

## 📞 技术支持

如有问题，请联系开发团队或查看项目文档。

