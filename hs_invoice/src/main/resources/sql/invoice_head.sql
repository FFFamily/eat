-- 发票抬头表
CREATE TABLE `invoice_head` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `account_id` varchar(32) NOT NULL COMMENT '账号ID，关联用户账号',
  `type` varchar(20) NOT NULL COMMENT '发票抬头类型：PERSONAL-个人，ENTERPRISE-企业',
  `title` varchar(200) NOT NULL COMMENT '发票抬头名称，个人为姓名，企业为企业全称',
  `tax_number` varchar(50) DEFAULT NULL COMMENT '纳税人识别号，企业必填，个人可不填',
  `registered_address` varchar(500) DEFAULT NULL COMMENT '注册地址，企业必填，个人可不填',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `bank_name` varchar(100) DEFAULT NULL COMMENT '开户行名称',
  `bank_code` varchar(20) DEFAULT NULL COMMENT '联行号',
  `bank_account` varchar(50) DEFAULT NULL COMMENT '银行账号',
  `is_default` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否为默认抬头：0-否，1-是',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(32) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否删除：0-否，1-是',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`) COMMENT '账号ID索引',
  KEY `idx_account_default` (`account_id`, `is_default`) COMMENT '账号默认抬头索引',
  KEY `idx_create_time` (`create_time`) COMMENT '创建时间索引',
  KEY `idx_deleted` (`deleted`) COMMENT '删除标识索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发票抬头表';

-- 添加唯一约束：每个账号只能有一个默认抬头
ALTER TABLE `invoice_head` 
ADD CONSTRAINT `uk_account_default` UNIQUE KEY (`account_id`, `is_default`) 
WHERE `is_default` = 1 AND `deleted` = 0;

-- 插入测试数据（可选）
INSERT INTO `invoice_head` (
  `id`, 
  `account_id`, 
  `type`, 
  `title`, 
  `tax_number`, 
  `registered_address`, 
  `phone`, 
  `bank_name`, 
  `bank_code`, 
  `bank_account`, 
  `is_default`
) VALUES 
(
  'test_head_001',
  'test_account_001',
  'PERSONAL',
  '张三',
  NULL,
  NULL,
  '13800138000',
  NULL,
  NULL,
  NULL,
  1
),
(
  'test_head_002',
  'test_account_001',
  'ENTERPRISE',
  '北京科技有限公司',
  '91110000123456789X',
  '北京市朝阳区xxx街道xxx号',
  '010-12345678',
  '中国工商银行北京分行',
  '102100000000',
  '1234567890123456789',
  0
); 