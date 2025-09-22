-- 回收发票相关表结构（简化版）

-- 删除已存在的表
DROP TABLE IF EXISTS `recycle_invoice_detail`;
DROP TABLE IF EXISTS `recycle_invoice`;

-- 创建回收发票表
CREATE TABLE `recycle_invoice` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `invoice_no` varchar(100) DEFAULT NULL COMMENT '发票号码',
  `invoice_type` varchar(20) DEFAULT NULL COMMENT '发票类型（进项、销项）',
  `invoice_bank` varchar(200) DEFAULT NULL COMMENT '开票银行',
  `planned_invoice_time` datetime DEFAULT NULL COMMENT '计划开票时间',
  `status` varchar(20) DEFAULT 'pending' COMMENT '状态（pending-待开票，invoiced-已开票）',
  `processor` varchar(100) DEFAULT NULL COMMENT '经办人',
  `invoice_time` datetime DEFAULT NULL COMMENT '开票时间',
  `total_amount` decimal(15,2) DEFAULT NULL COMMENT '总金额',
  `tax_amount` decimal(15,2) DEFAULT NULL COMMENT '税额',
  `amount_without_tax` decimal(15,2) DEFAULT NULL COMMENT '不含税金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_invoice_no` (`invoice_no`),
  KEY `idx_invoice_type` (`invoice_type`),
  KEY `idx_status` (`status`),
  KEY `idx_processor` (`processor`),
  KEY `idx_planned_invoice_time` (`planned_invoice_time`),
  KEY `idx_invoice_time` (`invoice_time`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收发票表';

-- 创建回收发票明细表
CREATE TABLE `recycle_invoice_detail` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `invoice_id` varchar(64) NOT NULL COMMENT '发票ID',
  `order_no` varchar(100) DEFAULT NULL COMMENT '订单编号',
  `order_total_amount` decimal(15,2) DEFAULT NULL COMMENT '订单总金额',
  `order_actual_invoice` decimal(15,2) DEFAULT NULL COMMENT '订单实开发票',
  `order_should_invoice` decimal(15,2) DEFAULT NULL COMMENT '订单应开发票',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_invoice_id` (`invoice_id`),
  KEY `idx_order_no` (`order_no`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_invoice_detail_invoice` FOREIGN KEY (`invoice_id`) REFERENCES `recycle_invoice` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收发票明细表';
