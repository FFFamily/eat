-- =============================================
-- 积分全局配置表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `point_global_config`;

-- =============================================
-- 积分全局配置表
-- =============================================
CREATE TABLE `point_global_config` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `point_ratio` decimal(10,2) NOT NULL COMMENT '积分比例（例如：1元 = 10积分，则比例为10）',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分全局配置表';

