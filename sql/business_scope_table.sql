-- =============================================
-- 经营范围表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `business_scope`;

-- =============================================
-- 经营范围表
-- =============================================
CREATE TABLE `business_scope` (
  `id` varchar(64) NOT NULL COMMENT '经营范围ID',
  `no` varchar(64) DEFAULT NULL COMMENT '编号',
  `sort_num` int DEFAULT 0 COMMENT '排序号',
  `good_type` varchar(50) DEFAULT NULL COMMENT '货物类型',
  `good_name` varchar(255) DEFAULT NULL COMMENT '货物名称',
  `good_model` varchar(100) DEFAULT NULL COMMENT '规格型号',
  `good_remark` text DEFAULT NULL COMMENT '货物备注',
  `public_price` decimal(15,2) DEFAULT NULL COMMENT '公示价格',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_no` (`no`),
  KEY `idx_sort_num` (`sort_num`),
  KEY `idx_good_type` (`good_type`),
  KEY `idx_good_name` (`good_name`),
  KEY `idx_public_price` (`public_price`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='经营范围表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 货物类型和名称复合索引
CREATE INDEX `idx_good_type_name` ON `business_scope` (`good_type`, `good_name`);

-- 价格范围查询索引
CREATE INDEX `idx_price_range` ON `business_scope` (`public_price`, `good_type`);

-- 排序索引
CREATE INDEX `idx_sort_order` ON `business_scope` (`sort_num`, `good_type`);

-- =============================================
-- 插入示例数据（可选）
-- =============================================

-- 插入示例经营范围数据
INSERT INTO `business_scope` (`id`, `no`, `sort_num`, `good_type`, `good_name`, `good_model`, `good_remark`, `public_price`, `create_time`, `update_time`, `create_by`, `update_by`, `is_deleted`) VALUES 
('BS001', 'BS2024001', 1, '废金属', '废铁', '普通废铁', '含杂质不超过5%', 2.50, NOW(), NOW(), 'system', 'system', '0'),
('BS002', 'BS2024002', 2, '废金属', '废铜', '电线废铜', '纯度不低于90%', 15.00, NOW(), NOW(), 'system', 'system', '0'),
('BS003', 'BS2024003', 3, '废金属', '废铝', '铝型材废料', '无严重氧化', 8.00, NOW(), NOW(), 'system', 'system', '0'),
('BS004', 'BS2024004', 1, '废纸', '废纸', '混合废纸', '含水率不超过15%', 0.80, NOW(), NOW(), 'system', 'system', '0'),
('BS005', 'BS2024005', 2, '废纸', '废纸', '纸板废料', '无严重污染', 1.20, NOW(), NOW(), 'system', 'system', '0'),
('BS006', 'BS2024006', 1, '废塑料', '废塑料', 'PET瓶', '无油污，无标签', 1.50, NOW(), NOW(), 'system', 'system', '0'),
('BS007', 'BS2024007', 2, '废塑料', '废塑料', 'PP料', '无杂质，无污染', 2.80, NOW(), NOW(), 'system', 'system', '0'),
('BS008', 'BS2024008', 1, '废玻璃', '废玻璃', '透明玻璃', '无破损，无杂质', 0.30, NOW(), NOW(), 'system', 'system', '0'); 