-- =============================================
-- 回收合同管理相关表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `recycle_contract_item`;
DROP TABLE IF EXISTS `recycle_contract`;

-- =============================================
-- 回收合同主表
-- =============================================
CREATE TABLE `recycle_contract` (
  `id` varchar(64) NOT NULL COMMENT '合同ID',
  `name` varchar(255) DEFAULT NULL COMMENT '合同名称',
  `type` varchar(50) DEFAULT NULL COMMENT '合同类型',
  `partner` varchar(255) DEFAULT NULL COMMENT '合作方',
  `start_time` datetime DEFAULT NULL COMMENT '起始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `main_bank_card` varchar(50) DEFAULT NULL COMMENT '主银行卡号',
  `invoice_info` text DEFAULT NULL COMMENT '开票信息',
  `pay_node` varchar(100) DEFAULT NULL COMMENT '走款节点',
  `invoice_node` varchar(100) DEFAULT NULL COMMENT '开票节点',
  `total_amount` decimal(15,2) DEFAULT NULL COMMENT '合同总金额',
  `pool` varchar(100) DEFAULT NULL COMMENT '合同资金池',
  `status` varchar(20) DEFAULT 'DRAFT' COMMENT '合同状态(DRAFT-草稿,ACTIVE-生效,COMPLETED-完成,TERMINATED-终止)',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_type` (`type`),
  KEY `idx_partner` (`partner`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收合同主表';

-- =============================================
-- 回收合同项目明细表
-- =============================================
CREATE TABLE `recycle_contract_item` (
  `id` varchar(64) NOT NULL COMMENT '项目ID',
  `recycle_contract_id` varchar(64) NOT NULL COMMENT '回收合同ID',
  `good_no` varchar(64) DEFAULT NULL COMMENT '货物编号',
  `good_type` varchar(50) DEFAULT NULL COMMENT '货物分类',
  `good_name` varchar(255) DEFAULT NULL COMMENT '货物名称',
  `good_model` varchar(100) DEFAULT NULL COMMENT '货物型号',
  `good_count` int DEFAULT NULL COMMENT '货物数量',
  `good_price` decimal(10,2) DEFAULT NULL COMMENT '货物单价',
  `good_total_price` decimal(15,2) DEFAULT NULL COMMENT '货物总价',
  `good_remark` text DEFAULT NULL COMMENT '货物备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` tinyint DEFAULT 0 COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`recycle_contract_id`),
  KEY `idx_good_no` (`good_no`),
  KEY `idx_good_type` (`good_type`),
  KEY `idx_good_name` (`good_name`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_contract_item_contract` FOREIGN KEY (`recycle_contract_id`) REFERENCES `recycle_contract` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收合同项目明细表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 合同主表复合索引
CREATE INDEX `idx_contract_composite` ON `recycle_contract` (`type`, `partner`, `status`);
CREATE INDEX `idx_contract_date_range` ON `recycle_contract` (`start_time`, `end_time`);
CREATE INDEX `idx_contract_amount_range` ON `recycle_contract` (`total_amount`, `status`);

-- 合同项目表复合索引
CREATE INDEX `idx_contract_item_composite` ON `recycle_contract_item` (`recycle_contract_id`, `good_type`);
CREATE INDEX `idx_contract_item_good_composite` ON `recycle_contract_item` (`recycle_contract_id`, `good_name`, `good_model`);

-- =============================================
-- 插入示例数据（可选）
-- =============================================

-- 插入示例回收合同数据
INSERT INTO `recycle_contract` (`id`, `name`, `type`, `partner`, `start_time`, `end_time`, `main_bank_card`, `invoice_info`, `pay_node`, `invoice_node`, `total_amount`, `pool`, `status`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES 
('RC001', '废金属回收合同2024-001', '废金属回收', 'ABC废料回收公司', '2024-01-01 00:00:00', '2024-12-31 23:59:59', '6222021234567890123', '增值税专用发票，税号：123456789012345678', '月结30天', '次月15日前', 50000.00, '回收资金池A', 'ACTIVE', NOW(), NOW(), 'system', 'system', 0),
('RC002', '废纸回收合同2024-002', '废纸回收', 'XYZ纸业回收公司', '2024-02-01 00:00:00', '2024-11-30 23:59:59', '6222021234567890456', '增值税普通发票，税号：123456789012345679', '月结45天', '次月20日前', 30000.00, '回收资金池B', 'ACTIVE', NOW(), NOW(), 'system', 'system', 0),
('RC003', '废塑料回收合同2024-003', '废塑料回收', 'DEF塑料回收公司', '2024-03-01 00:00:00', '2024-10-31 23:59:59', '6222021234567890789', '增值税专用发票，税号：123456789012345680', '季结', '季度末次月10日前', 80000.00, '回收资金池C', 'DRAFT', NOW(), NOW(), 'system', 'system', 0);

-- 插入示例回收合同项目数据
INSERT INTO `recycle_contract_item` (`id`, `recycle_contract_id`, `good_no`, `good_type`, `good_name`, `good_model`, `good_count`, `good_price`, `good_total_price`, `good_remark`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES 
('RCI001', 'RC001', 'GM001', '废铁', '废铁', '普通废铁', 1000, 2.50, 2500.00, '含杂质不超过5%', NOW(), NOW(), 'system', 'system', 0),
('RCI002', 'RC001', 'GM002', '废铜', '废铜', '电线废铜', 500, 15.00, 7500.00, '纯度不低于90%', NOW(), NOW(), 'system', 'system', 0),
('RCI003', 'RC001', 'GM003', '废铝', '废铝', '铝型材废料', 800, 8.00, 6400.00, '无严重氧化', NOW(), NOW(), 'system', 'system', 0),
('RCI004', 'RC002', 'GP001', '废纸', '废纸', '混合废纸', 2000, 0.80, 1600.00, '含水率不超过15%', NOW(), NOW(), 'system', 'system', 0),
('RCI005', 'RC002', 'GP002', '废纸', '废纸', '纸板废料', 1500, 1.20, 1800.00, '无严重污染', NOW(), NOW(), 'system', 'system', 0),
('RCI006', 'RC003', 'GPL001', '废塑料', '废塑料', 'PET瓶片', 3000, 1.50, 4500.00, '颜色分类清晰', NOW(), NOW(), 'system', 'system', 0),
('RCI007', 'RC003', 'GPL002', '废塑料', '废塑料', 'HDPE废料', 2500, 2.00, 5000.00, '无严重老化', NOW(), NOW(), 'system', 'system', 0);

-- =============================================
-- 表结构说明
-- =============================================
/*
回收合同主表 (recycle_contract):
- 存储回收合同的基本信息，包括合同名称、类型、合作方、时间范围、金额等
- 支持合同状态管理（草稿、生效、完成、终止）
- 包含财务相关信息（银行卡、开票信息、走款节点等）

回收合同项目明细表 (recycle_contract_item):
- 存储合同下的具体货物项目信息
- 支持多种货物类型（废铁、废铜、废纸、废塑料等）
- 包含货物的数量、单价、总价等详细信息
- 通过外键关联到合同主表，支持级联删除

索引设计:
- 主键索引：id
- 业务索引：合同名称、类型、合作方、状态等
- 复合索引：优化常用查询场景
- 外键约束：保证数据完整性
*/ 