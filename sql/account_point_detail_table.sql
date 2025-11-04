-- =============================================
-- 账户积分详情表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `account_point_detail`;

-- =============================================
-- 账户积分详情表
-- =============================================
CREATE TABLE `account_point_detail` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `account_id` varchar(64) NOT NULL COMMENT '账户ID',
  `before_point` bigint DEFAULT NULL COMMENT '变更前积分',
  `after_point` bigint DEFAULT NULL COMMENT '变更后积分',
  `change_direction` varchar(10) DEFAULT NULL COMMENT '变更方向（ADD-增加，SUB-减少）',
  `change_reason` varchar(255) DEFAULT NULL COMMENT '变更原因',
  `change_point` bigint NOT NULL COMMENT '变更数量',
  `change_type` varchar(50) DEFAULT NULL COMMENT '变更类型（EARN-获得，USE-使用，REFUND-退款等）',
  `remark` text DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_change_type` (`change_type`),
  KEY `idx_change_direction` (`change_direction`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户积分详情表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 账户ID和变更类型复合索引（用于查询某个账户的特定类型积分变更记录）
CREATE INDEX `idx_account_type` ON `account_point_detail` (`account_id`, `change_type`);

-- 账户ID和创建时间复合索引（用于按时间排序查询某个账户的积分变更记录）
CREATE INDEX `idx_account_create_time` ON `account_point_detail` (`account_id`, `create_time`);

