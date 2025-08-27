-- 用户银行卡信息表
CREATE TABLE `user_bank_card` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  
  -- ========== 银行卡基本信息 ==========
  `account_id` varchar(64) NOT NULL COMMENT '账户ID',
  `bank_name` varchar(100) NOT NULL COMMENT '开户行',
  `card_number` varchar(50) NOT NULL COMMENT '银行卡号',
  `bank_code` varchar(20) DEFAULT NULL COMMENT '联行号',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否默认：0-否，1-是',
  
  -- ========== 基础字段 ==========
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`) COMMENT '账户ID索引',
  KEY `idx_card_number` (`card_number`) COMMENT '银行卡号索引',
  KEY `idx_is_default` (`is_default`) COMMENT '是否默认索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  UNIQUE KEY `uk_account_card` (`account_id`, `card_number`) COMMENT '账户和卡号唯一索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户银行卡信息表';

-- 创建复合索引优化查询性能
CREATE INDEX `idx_account_default` ON `user_bank_card` (`account_id`, `is_default`);
CREATE INDEX `idx_bank_name` ON `user_bank_card` (`bank_name`);

-- 插入示例数据
INSERT INTO `user_bank_card` (`id`, `account_id`, `bank_name`, `card_number`, `bank_code`, `is_default`, `create_by`, `update_by`) VALUES
('1', '1001', '中国银行', '6222021234567890123', '104100000004', 1, 'system', 'system'),
('2', '1001', '工商银行', '6222021234567890456', '102100099996', 0, 'system', 'system'),
('3', '1002', '建设银行', '6222021234567890789', '105100000013', 1, 'system', 'system');