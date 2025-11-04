-- =============================================
-- 账户积分使用详情表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `account_point_use_detail`;

-- =============================================
-- 账户积分使用详情表
-- =============================================
CREATE TABLE `account_point_use_detail` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `account_id` varchar(64) NOT NULL COMMENT '账户ID',
  `point_goods_id` varchar(64) DEFAULT NULL COMMENT '积分商品ID',
  `exchange_code` varchar(64) DEFAULT NULL COMMENT '兑换码',
  `point` bigint NOT NULL COMMENT '消耗积分',
  `is_used` tinyint(1) DEFAULT 0 COMMENT '是否已使用（0-未使用，1-已使用）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_point_goods_id` (`point_goods_id`),
  KEY `idx_exchange_code` (`exchange_code`),
  KEY `idx_is_used` (`is_used`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户积分使用详情表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 账户ID和是否已使用复合索引（用于查询某个账户的已使用或未使用记录）
CREATE INDEX `idx_account_is_used` ON `account_point_use_detail` (`account_id`, `is_used`);

-- 账户ID和积分商品ID复合索引（用于查询某个账户兑换的特定商品记录）
CREATE INDEX `idx_account_goods` ON `account_point_use_detail` (`account_id`, `point_goods_id`);

-- 兑换码唯一索引（确保兑换码唯一性）
CREATE UNIQUE INDEX `uk_exchange_code` ON `account_point_use_detail` (`exchange_code`);

