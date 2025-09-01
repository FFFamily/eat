-- 用户银行卡信息表
CREATE TABLE `account_bank_card` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `account_id` varchar(32) NOT NULL COMMENT '账户ID',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户行',
  `card_number` varchar(30) DEFAULT NULL COMMENT '银行卡号',
  `bank_code` varchar(20) DEFAULT NULL COMMENT '联行号',
  `is_default` varchar(1) DEFAULT '0' COMMENT '是否默认（0-否，1-是）',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '逻辑删除（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_card_number` (`card_number`),
  KEY `idx_is_deleted` (`is_deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户银行卡信息表'; 