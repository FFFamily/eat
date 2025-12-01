-- 账户客户关系表
CREATE TABLE `account_customer` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `account_id` varchar(32) NOT NULL COMMENT '账户ID',
  `customer_account_id` varchar(32) NOT NULL COMMENT '客户账户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '逻辑删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_customer_account_id` (`customer_account_id`),
  KEY `idx_is_deleted` (`is_deleted`),
  UNIQUE KEY `uk_account_customer` (`account_id`, `customer_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户客户关系表';

