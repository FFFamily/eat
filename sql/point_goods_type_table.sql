-- =============================================
-- 积分商品分类表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `point_goods_type`;

-- =============================================
-- 积分商品分类表
-- =============================================
CREATE TABLE `point_goods_type` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `type_name` varchar(100) NOT NULL COMMENT '分类名称',
  `sort_num` int DEFAULT 0 COMMENT '分类排序',
  `status` varchar(1) DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
  `description` text DEFAULT NULL COMMENT '分类描述',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_type_name` (`type_name`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_num` (`sort_num`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商品分类表';

-- =============================================
-- 创建索引优化查询性能
-- =============================================

-- 状态和排序复合索引（用于查询启用的分类并按排序号排序）
CREATE INDEX `idx_status_sort` ON `point_goods_type` (`status`, `sort_num`);

