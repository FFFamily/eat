-- 经办人表
CREATE TABLE `processor` (
  `id` varchar(32) NOT NULL COMMENT '主键ID',
  `account_id` varchar(32) DEFAULT NULL COMMENT '账号ID',
  `no` varchar(50) DEFAULT NULL COMMENT '编号',
  `name` varchar(100) NOT NULL COMMENT '经办人名称',
  `phone` varchar(20) DEFAULT NULL COMMENT '电话',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `address` varchar(255) DEFAULT NULL COMMENT '住址',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_no` (`no`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='经办人表'; 