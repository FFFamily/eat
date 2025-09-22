# 回收发票表结构说明

## 表结构概览

### 1. recycle_invoice（回收发票表）
存储发票的基本信息，包括发票号码、类型、金额等。

### 2. recycle_invoice_detail（回收发票明细表）
存储发票的明细信息，关联具体的订单信息。

## 详细字段说明

### recycle_invoice 表字段

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | varchar | 64 | NO | - | 主键ID |
| invoice_no | varchar | 100 | YES | NULL | 发票号码（唯一） |
| invoice_type | varchar | 20 | YES | NULL | 发票类型（进项、销项） |
| invoice_bank | varchar | 200 | YES | NULL | 开票银行 |
| planned_invoice_time | datetime | - | YES | NULL | 计划开票时间 |
| status | varchar | 20 | YES | pending | 状态（pending-待开票，invoiced-已开票） |
| processor | varchar | 100 | YES | NULL | 经办人 |
| invoice_time | datetime | - | YES | NULL | 开票时间 |
| total_amount | decimal | 15,2 | YES | NULL | 总金额 |
| tax_amount | decimal | 15,2 | YES | NULL | 税额 |
| amount_without_tax | decimal | 15,2 | YES | NULL | 不含税金额 |
| create_time | datetime | - | YES | NULL | 创建时间 |
| update_time | datetime | - | YES | NULL | 更新时间 |
| create_by | varchar | 64 | YES | NULL | 创建人 |
| update_by | varchar | 64 | YES | NULL | 更新人 |
| is_deleted | varchar | 1 | YES | 0 | 是否删除(0-未删除,1-已删除) |

### recycle_invoice_detail 表字段

| 字段名 | 类型 | 长度 | 允许NULL | 默认值 | 说明 |
|--------|------|------|----------|--------|------|
| id | varchar | 64 | NO | - | 主键ID |
| invoice_id | varchar | 64 | NO | - | 发票ID（外键） |
| order_no | varchar | 100 | YES | NULL | 订单编号 |
| order_total_amount | decimal | 15,2 | YES | NULL | 订单总金额 |
| order_actual_invoice | decimal | 15,2 | YES | NULL | 订单实开发票 |
| order_should_invoice | decimal | 15,2 | YES | NULL | 订单应开发票 |
| create_time | datetime | - | YES | NULL | 创建时间 |
| update_time | datetime | - | YES | NULL | 更新时间 |
| create_by | varchar | 64 | YES | NULL | 创建人 |
| update_by | varchar | 64 | YES | NULL | 更新人 |
| is_deleted | varchar | 1 | YES | 0 | 是否删除(0-未删除,1-已删除) |

## 索引设计

### recycle_invoice 表索引
- `PRIMARY KEY (id)` - 主键索引
- `UNIQUE KEY uk_invoice_no (invoice_no)` - 发票号码唯一索引
- `KEY idx_invoice_type (invoice_type)` - 发票类型索引
- `KEY idx_status (status)` - 状态索引
- `KEY idx_processor (processor)` - 经办人索引
- `KEY idx_planned_invoice_time (planned_invoice_time)` - 计划开票时间索引
- `KEY idx_invoice_time (invoice_time)` - 开票时间索引
- `KEY idx_create_time (create_time)` - 创建时间索引

### recycle_invoice_detail 表索引
- `PRIMARY KEY (id)` - 主键索引
- `KEY idx_invoice_id (invoice_id)` - 发票ID索引
- `KEY idx_order_no (order_no)` - 订单编号索引
- `KEY idx_create_time (create_time)` - 创建时间索引

## 外键约束

### recycle_invoice_detail 表外键
- `CONSTRAINT fk_invoice_detail_invoice FOREIGN KEY (invoice_id) REFERENCES recycle_invoice (id) ON DELETE CASCADE ON UPDATE CASCADE`
  - 当发票被删除时，相关的明细记录也会被级联删除
  - 当发票ID更新时，相关的明细记录也会被级联更新

## 状态枚举

### 发票状态
- `pending` - 待开票（默认状态）
- `invoiced` - 已开票

### 发票类型
- `进项` - 进项发票
- `销项` - 销项发票

## 数据类型说明

### 金额字段
- 使用 `decimal(15,2)` 类型，确保金额精度
- 支持最大13位整数和2位小数

### 时间字段
- 使用 `datetime` 类型
- 格式：`YYYY-MM-DD HH:MM:SS`

### 字符串字段
- 主键ID：`varchar(64)` - 使用雪花算法生成
- 发票号码：`varchar(100)` - 支持较长的发票号码
- 其他字段根据业务需求设置合适长度

## 建表SQL文件

### 1. recycle_invoice_tables.sql
- 完整版建表SQL
- 包含数据字典插入语句
- 适合生产环境使用

### 2. recycle_invoice_simple.sql
- 简化版建表SQL
- 不包含数据字典
- 适合开发环境快速建表

### 3. recycle_invoice_example_data.sql
- 示例数据插入SQL
- 包含测试用的发票和明细数据
- 适合开发测试使用

## 使用建议

1. **生产环境**：使用 `recycle_invoice_tables.sql`
2. **开发环境**：使用 `recycle_invoice_simple.sql` + `recycle_invoice_example_data.sql`
3. **数据迁移**：先执行建表SQL，再执行示例数据SQL
4. **索引优化**：根据实际查询需求调整索引策略
5. **数据备份**：定期备份发票相关数据，确保数据安全

## 注意事项

1. 发票号码必须唯一，建议在应用层进行唯一性校验
2. 金额字段使用decimal类型，避免浮点数精度问题
3. 外键约束确保数据完整性，但会影响删除性能
4. 逻辑删除字段 `is_deleted` 用于软删除，不实际删除数据
5. 时间字段建议使用应用层自动填充，确保数据一致性
