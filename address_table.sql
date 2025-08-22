-- 地址管理表
CREATE TABLE `address` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `account_id` varchar(32) DEFAULT NULL COMMENT '用户ID',
  `category` varchar(50) DEFAULT NULL COMMENT '分类',
  `real_address` varchar(500) DEFAULT NULL COMMENT '地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '逻辑删除标识(0:未删除 1:已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_category` (`category`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地址管理表'; 