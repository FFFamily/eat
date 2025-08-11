-- =============================================
-- 合同管理相关表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `sys_contract_item`;
DROP TABLE IF EXISTS `sys_contract`;

-- =============================================
-- 系统合同主表
-- =============================================
CREATE TABLE `sys_contract` (
  `id` varchar(64) NOT NULL COMMENT '合同ID',
  `name` varchar(255) DEFAULT NULL COMMENT '合同名称',
  `recognition_code` varchar(64) DEFAULT NULL COMMENT '合同识别号',
  `code` varchar(64) DEFAULT NULL COMMENT '合同编码',
  `type` varchar(50) DEFAULT NULL COMMENT '合同类型',
  `start_date` datetime DEFAULT NULL COMMENT '起始日期',
  `end_date` datetime DEFAULT NULL COMMENT '结束日期',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '付款方式',
  `invoice_method` varchar(50) DEFAULT NULL COMMENT '开票方式',
  `user_id` varchar(64) DEFAULT NULL COMMENT '签署用户ID',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_recognition_code` (`recognition_code`),
  KEY `idx_code` (`code`),
  KEY `idx_type` (`type`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统合同主表';

-- =============================================
-- 系统合同明细表
-- =============================================
CREATE TABLE `sys_contract_item` (
  `id` varchar(64) NOT NULL COMMENT '明细ID',
  `contract_id` varchar(64) NOT NULL COMMENT '合同ID',
  -- 回收货物信息
  `recycle_good_id` varchar(64) DEFAULT NULL COMMENT '回收货物ID',
  `recycle_good_name` varchar(255) DEFAULT NULL COMMENT '回收货物名称',
  `recycle_good_specification_model` varchar(255) DEFAULT NULL COMMENT '回收货物规格型号',
  `recycle_good_transport_mode` varchar(50) DEFAULT NULL COMMENT '回收货物运输模式',
  `recycle_good_subtotal` decimal(15,2) DEFAULT NULL COMMENT '回收货物金额',
  -- 租赁设备信息
  `lease_good_id` varchar(64) DEFAULT NULL COMMENT '租赁设备ID',
  `lease_good_name` varchar(255) DEFAULT NULL COMMENT '租赁设备名称',
  `lease_good_deposit` decimal(15,2) DEFAULT NULL COMMENT '租赁设备押金',
  `lease_good_subtotal` decimal(15,2) DEFAULT NULL COMMENT '租赁设备金额',
  `lease_good_install_date` datetime DEFAULT NULL COMMENT '租赁设备安装日期',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_recycle_good_id` (`recycle_good_id`),
  KEY `idx_lease_good_id` (`lease_good_id`),
  CONSTRAINT `fk_contract_item_contract` FOREIGN KEY (`contract_id`) REFERENCES `sys_contract` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='系统合同明细表';

-- =============================================
-- 创建索引优化查询性能
-- =============================================

-- 合同主表复合索引
CREATE INDEX `idx_contract_composite` ON `sys_contract` (`type`, `user_id`, `create_time`);
CREATE INDEX `idx_contract_date_range` ON `sys_contract` (`start_date`, `end_date`);

-- 合同明细表复合索引
CREATE INDEX `idx_contract_item_composite` ON `sys_contract_item` (`contract_id`, `recycle_good_id`);
CREATE INDEX `idx_contract_item_lease_composite` ON `sys_contract_item` (`contract_id`, `lease_good_id`);

-- =============================================
-- 插入示例数据（可选）
-- =============================================

-- 插入示例合同数据
INSERT INTO `sys_contract` (`id`, `name`, `recognition_code`, `code`, `type`, `start_date`, `end_date`, `payment_method`, `invoice_method`, `user_id`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES 
('contract_001', '废料回收合同示例', 'RC2024001', 'CONTRACT001', '回收合同', '2024-01-01 00:00:00', '2024-12-31 23:59:59', '月结', '增值税专用发票', 'user_001', NOW(), NOW(), 'system', 'system', '0'),
('contract_002', '设备租赁合同示例', 'LC2024001', 'CONTRACT002', '租赁合同', '2024-02-01 00:00:00', '2024-11-30 23:59:59', '季结', '增值税普通发票', 'user_002', NOW(), NOW(), 'system', 'system', '0');

-- 插入示例合同明细数据
INSERT INTO `sys_contract_item` (`id`, `contract_id`, `recycle_good_id`, `recycle_good_name`, `recycle_good_specification_model`, `recycle_good_transport_mode`, `recycle_good_subtotal`, `lease_good_id`, `lease_good_name`, `lease_good_deposit`, `lease_good_subtotal`, `lease_good_install_date`) VALUES 
('item_001', 'contract_001', 'good_001', '废铁', '普通废铁', '汽运', 5000.00, NULL, NULL, NULL, NULL, NULL),
('item_002', 'contract_001', 'good_002', '废铜', '电线废铜', '汽运', 8000.00, NULL, NULL, NULL, NULL, NULL),
('item_003', 'contract_002', NULL, NULL, NULL, NULL, NULL, 'lease_001', '压缩设备', 2000.00, 1500.00, '2024-02-15 10:00:00'),
('item_004', 'contract_002', NULL, NULL, NULL, NULL, NULL, 'lease_002', '分拣设备', 3000.00, 2500.00, '2024-02-20 14:00:00');
