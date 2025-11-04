-- =============================================
-- 积分商品表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `point_goods`;

-- =============================================
-- 积分商品表
-- =============================================
CREATE TABLE `point_goods` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `type_id` varchar(64) DEFAULT NULL COMMENT '商品分类ID',
  `goods_name` varchar(200) NOT NULL COMMENT '商品名称',
  `goods_image` varchar(500) DEFAULT NULL COMMENT '商品图片',
  `goods_description` text DEFAULT NULL COMMENT '商品描述',
  `point_price` decimal(10,2) NOT NULL COMMENT '积分价格（需要消耗的积分）',
  `stock` int DEFAULT 0 COMMENT '库存数量',
  `status` varchar(1) DEFAULT '1' COMMENT '状态（0-下架，1-上架）',
  `sort_num` int DEFAULT 0 COMMENT '排序',
  `goods_detail` text DEFAULT NULL COMMENT '商品详情',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_goods_name` (`goods_name`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_num` (`sort_num`),
  KEY `idx_point_price` (`point_price`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='积分商品表';

-- =============================================
-- 创建索引优化查询性能
-- =============================================

-- 分类ID和状态复合索引（用于查询某个分类下的上架商品）
CREATE INDEX `idx_type_status` ON `point_goods` (`type_id`, `status`);

-- 状态和排序复合索引（用于查询上架商品并按排序号排序）
CREATE INDEX `idx_status_sort` ON `point_goods` (`status`, `sort_num`);

-- 分类ID、状态和排序复合索引（用于查询某个分类下的上架商品并按排序号排序）
CREATE INDEX `idx_type_status_sort` ON `point_goods` (`type_id`, `status`, `sort_num`);

-- =============================================
-- 外键约束（可选，根据实际需求决定是否添加）
-- =============================================
-- ALTER TABLE `point_goods` ADD CONSTRAINT `fk_goods_type` FOREIGN KEY (`type_id`) REFERENCES `point_goods_type` (`id`) ON DELETE SET NULL ON UPDATE CASCADE;

