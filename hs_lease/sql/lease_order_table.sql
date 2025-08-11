-- 租赁订单主表
CREATE TABLE `lease_order` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `order_no` varchar(64) NOT NULL COMMENT '订单编号',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `user_name` varchar(100) NOT NULL COMMENT '用户名称',
  `status` varchar(32) NOT NULL DEFAULT 'PENDING_REVIEW' COMMENT '订单状态：PENDING_REVIEW-待审核，LEASING-租赁中，PENDING_INVOICE-待开票，COMPLETED-已完成',
  `total_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '订单总金额',
  `paid_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '已支付金额',
  `deposit_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '押金金额',
  `remark` varchar(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租赁订单主表';

-- 租赁订单明细表
CREATE TABLE `lease_order_item` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `order_id` varchar(64) NOT NULL COMMENT '订单ID',
  `good_id` varchar(64) NOT NULL COMMENT '商品ID',
  `good_name` varchar(200) NOT NULL COMMENT '商品名称',
  `good_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '商品单价',
  `quantity` int NOT NULL DEFAULT '1' COMMENT '租赁数量',
  `lease_start_time` datetime NOT NULL COMMENT '租赁开始时间',
  `lease_end_time` datetime NOT NULL COMMENT '租赁结束时间',
  `lease_days` int NOT NULL DEFAULT '1' COMMENT '租赁天数',
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '小计金额',
  `deposit_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '押金金额',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '订单项状态：0-正常，1-已取消',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_good_id` (`good_id`),
  KEY `idx_lease_time` (`lease_start_time`, `lease_end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='租赁订单明细表';

-- 订单状态流转记录表（可选，用于记录状态变更历史）
CREATE TABLE `lease_order_status_log` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `order_id` varchar(64) NOT NULL COMMENT '订单ID',
  `from_status` varchar(32) DEFAULT NULL COMMENT '原状态',
  `to_status` varchar(32) NOT NULL COMMENT '新状态',
  `operator_id` varchar(64) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) DEFAULT NULL COMMENT '操作人姓名',
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型：REVIEW-审核，FINISH_LEASING-完成租赁，COMPLETE_INVOICE-完成开票，CANCEL-取消',
  `remark` varchar(500) DEFAULT NULL COMMENT '操作备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单状态流转记录表';

-- 插入初始数据（可选）
INSERT INTO `lease_order_status_log` (`id`, `order_id`, `from_status`, `to_status`, `operation_type`, `remark`) VALUES 
('1', 'system', NULL, 'PENDING_REVIEW', 'CREATE', '系统初始化：订单创建');

-- 创建索引优化查询性能
CREATE INDEX `idx_lease_order_composite` ON `lease_order` (`user_id`, `status`, `create_time`);
CREATE INDEX `idx_lease_order_item_composite` ON `lease_order_item` (`order_id`, `good_id`); 