-- =============================================
-- 用户订单表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `user_order`;

-- =============================================
-- 用户订单表
-- =============================================
CREATE TABLE `user_order` (
  `id` varchar(64) NOT NULL COMMENT '订单ID',
  `no` varchar(64) DEFAULT NULL COMMENT '订单编号',
  `stage` varchar(50) DEFAULT NULL COMMENT '订单阶段',
  `status` varchar(50) DEFAULT NULL COMMENT '订单状态',
  `contract_id` varchar(64) DEFAULT NULL COMMENT '合同ID',
  `contract_no` varchar(100) DEFAULT NULL COMMENT '合同编号',
  `contract_name` varchar(255) DEFAULT NULL COMMENT '合同名称',
  `contract_partner` varchar(255) DEFAULT NULL COMMENT '合同合作方',
  `contract_partner_name` varchar(255) DEFAULT NULL COMMENT '合同合作方名称',
  `party_a` varchar(64) DEFAULT NULL COMMENT '甲方',
  `party_a_name` varchar(255) DEFAULT NULL COMMENT '甲方名称',
  `party_b` varchar(64) DEFAULT NULL COMMENT '乙方',
  `party_b_name` varchar(255) DEFAULT NULL COMMENT '乙方名称',
  `img_url` varchar(500) DEFAULT NULL COMMENT '订单图片URL',
  `location` varchar(255) DEFAULT NULL COMMENT '位置',
  `processor_id` varchar(64) DEFAULT NULL COMMENT '经办人ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_no` (`no`),
  KEY `idx_status` (`status`),
  KEY `idx_stage` (`stage`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_contract_no` (`contract_no`),
  KEY `idx_processor_id` (`processor_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户订单表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 订单状态和阶段复合索引
CREATE INDEX `idx_user_order_status_stage` ON `user_order` (`status`, `stage`);

-- 合同相关复合索引
CREATE INDEX `idx_user_order_contract_composite` ON `user_order` (`contract_id`, `status`);
CREATE INDEX `idx_user_order_contract_no_status` ON `user_order` (`contract_no`, `status`);

-- 经办人相关复合索引
CREATE INDEX `idx_user_order_processor_composite` ON `user_order` (`processor_id`, `status`, `stage`);

-- 时间范围查询复合索引
CREATE INDEX `idx_user_order_time_status` ON `user_order` (`create_time`, `status`);

-- 合作方查询复合索引
CREATE INDEX `idx_user_order_partner_composite` ON `user_order` (`contract_partner`, `status`);

-- =============================================
-- 插入示例数据（可选）
-- =============================================

-- 插入示例用户订单数据
INSERT INTO `user_order` (
  `id`, `no`, `stage`, `status`, `contract_id`, `contract_no`, `contract_name`, 
  `contract_partner`, `contract_partner_name`, `party_a`, `party_a_name`, 
  `party_b`, `party_b_name`, `img_url`, `location`, `processor_id`, 
  `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`
) VALUES 
(
  'UO001', 'UO202401010001', 'purchase', 'wait_transport', 'RC001', 'RC-2024-001', 
  '废金属回收合同2024-001', 'PARTNER001', 'ABC废料回收公司', 
  'PA001', '甲方公司A', 'PB001', '乙方公司B', 
  'https://example.com/order/img001.jpg', '北京市朝阳区xxx街道', 'PROC001', 
  NOW(), NOW(), 'system', 'system', '0'
),
(
  'UO002', 'UO202401010002', 'transport', 'wait_sorting', 'RC002', 'RC-2024-002', 
  '废纸回收合同2024-002', 'PARTNER002', 'XYZ纸业回收公司', 
  'PA002', '甲方公司C', 'PB002', '乙方公司D', 
  'https://example.com/order/img002.jpg', '上海市浦东新区xxx路', 'PROC002', 
  NOW(), NOW(), 'system', 'system', '0'
),
(
  'UO003', 'UO202401010003', 'processing', 'wait_warehousing', 'RC003', 'RC-2024-003', 
  '废塑料回收合同2024-003', 'PARTNER003', 'DEF塑料回收公司', 
  'PA003', '甲方公司E', 'PB003', '乙方公司F', 
  'https://example.com/order/img003.jpg', '广州市天河区xxx大道', 'PROC003', 
  NOW(), NOW(), 'system', 'system', '0'
),
(
  'UO004', 'UO202401010004', 'warehousing', 'completed', 'RC001', 'RC-2024-001', 
  '废金属回收合同2024-001', 'PARTNER001', 'ABC废料回收公司', 
  'PA001', '甲方公司A', 'PB001', '乙方公司B', 
  'https://example.com/order/img004.jpg', '深圳市南山区xxx街', 'PROC001', 
  NOW(), NOW(), 'system', 'system', '0'
),
(
  'UO005', 'UO202401010005', 'purchase', 'wait_transport', 'RC002', 'RC-2024-002', 
  '废纸回收合同2024-002', 'PARTNER002', 'XYZ纸业回收公司', 
  'PA002', '甲方公司C', 'PB002', '乙方公司D', 
  'https://example.com/order/img005.jpg', '杭州市西湖区xxx路', 'PROC002', 
  NOW(), NOW(), 'system', 'system', '0'
);

-- =============================================
-- 表结构说明
-- =============================================
/*
用户订单表 (user_order):
- 存储用户订单的基本信息，包括订单编号、状态、阶段等
- 关联合同信息，包括合同ID、合同编号、合同名称、合作方等
- 记录订单相关的甲方、乙方信息
- 支持订单图片和位置信息记录
- 关联经办人信息，便于订单管理和追踪
- 继承BaseEntity，包含创建时间、更新时间、创建人、更新人等审计字段
- 支持逻辑删除，保证数据安全性

字段说明:
- id: 订单唯一标识，主键
- no: 订单编号，业务编号（后台自动生成，格式：UO + yyyyMMdd + 序号，如：UO202401010001）
- stage: 订单阶段（枚举值：purchase-采购、transport-运输、processing-加工、warehousing-入库）
- status: 订单状态（枚举值：wait_transport-待运输、wait_sorting-待分拣、wait_warehousing-待入库、completed-已完成）
- contract_id: 关联的合同ID
- contract_no: 关联的合同编号
- contract_name: 关联的合同名称
- contract_partner: 合同合作方ID
- contract_partner_name: 合同合作方名称
- party_a: 甲方ID
- party_a_name: 甲方名称
- party_b: 乙方ID
- party_b_name: 乙方名称
- img_url: 订单相关图片URL
- location: 订单位置信息
- processor_id: 经办人ID
- create_time: 创建时间（自动填充）
- update_time: 更新时间（自动填充）
- create_by: 创建人ID
- update_by: 更新人ID
- is_deleted: 逻辑删除标记（0-未删除，1-已删除）

索引设计:
- 主键索引：id
- 单列索引：订单编号、状态、阶段、合同ID、合同编号、经办人ID、创建时间
- 复合索引：
  * 状态+阶段：用于按状态和阶段组合查询
  * 合同ID+状态：用于查询某合同下的订单列表
  * 合同编号+状态：用于按合同编号查询订单
  * 经办人ID+状态+阶段：用于查询经办人负责的订单
  * 创建时间+状态：用于时间范围内的订单统计
  * 合作方+状态：用于查询某合作方的订单

订单流程说明:
- 订单阶段流程：采购 -> 运输 -> 加工 -> 入库
- 订单状态流程：待运输 -> 待分拣 -> 待入库 -> 已完成
- 订单编号自动生成，格式：UO + yyyyMMdd + 4位序号（如：UO202401010001）
- 阶段和状态由系统自动控制，无法直接编辑
- 经办人名称(processor_name)不存储在数据库中，通过关联查询自动填充
- 创建用户订单时会同步创建一个采购类型的回收订单(recycle_order)
- 用户订单和回收订单在同一事务中创建，保证数据一致性

枚举类说明:
1. UserOrderStageEnum - 订单阶段枚举
   - PURCHASE (purchase, "采购")
   - TRANSPORT (transport, "运输")
   - PROCESSING (processing, "加工")
   - WAREHOUSING (warehousing, "入库")

2. UserOrderStatusEnum - 订单状态枚举
   - WAIT_TRANSPORT (wait_transport, "待运输")
   - WAIT_SORTING (wait_sorting, "待分拣")
   - WAIT_WAREHOUSING (wait_warehousing, "待入库")
   - COMPLETED (completed, "已完成")

使用场景:
1. 订单查询：根据订单编号、状态、阶段等条件查询
2. 合同关联查询：查询某个合同下的所有订单
3. 经办人管理：查询某个经办人负责的订单
4. 订单统计：按时间、状态、合作方等维度进行统计分析
5. 订单追踪：记录订单的位置、图片等信息，便于追踪管理
6. 订单流程控制：通过阶段和状态控制订单流转
7. 订单联动：创建用户订单时自动创建回收订单，实现业务流程自动化

注意事项:
1. processor_name 字段不在数据库表中，是通过关联查询获取的
2. Service层所有查询方法都会自动填充经办人名称，列表查询使用批量查询优化性能
3. 订单编号(no)由 UserOrderNoGenerator 工具类自动生成，建议设置唯一索引
4. 订单阶段和状态使用枚举值存储，保证数据一致性
5. 阶段和状态的变更由后台逻辑控制，前端不可直接修改
6. 如果需要与合同表建立外键关系，可以添加外键约束
7. 根据实际业务需求，可能需要添加更多字段，如：订单金额、数量等
8. 建议根据实际数据量和查询频率，定期优化索引
9. 订单编号生成器支持三种模式：序号模式、随机数模式、时间戳模式
10. 经办人信息存储在 processor 表中，通过 processor_id 关联
11. 创建用户订单时会自动创建一个采购类型的回收订单(type=PURCHASE, status=PROCESSING)
12. 用户订单和回收订单在同一事务中创建，任何一个失败都会回滚
13. 回收订单会复制用户订单的合同信息、甲乙方信息、经办人信息等
14. 建议在业务逻辑中维护用户订单和回收订单的关联关系
*/

