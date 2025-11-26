-- 账户服务范围表
CREATE TABLE `account_service_scope` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `account_id` varchar(32) NOT NULL COMMENT '账户ID',
  `province` varchar(50) DEFAULT NULL COMMENT '服务范围省',
  `city` varchar(50) DEFAULT NULL COMMENT '服务范围市',
  `district` varchar(50) DEFAULT NULL COMMENT '服务范围区',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '逻辑删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_province` (`province`),
  KEY `idx_city` (`city`),
  KEY `idx_district` (`district`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账户服务范围表';

