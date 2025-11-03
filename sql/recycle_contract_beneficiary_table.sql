-- =============================================
-- 回收合同受益人表结构
-- =============================================

-- 删除已存在的表
DROP TABLE IF EXISTS `recycle_contract_beneficiary`;

-- =============================================
-- 回收合同受益人表
-- =============================================
CREATE TABLE `recycle_contract_beneficiary` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `contract_id` varchar(64) NOT NULL COMMENT '合同ID（关联回收合同表）',
  `beneficiary_type` varchar(50) DEFAULT NULL COMMENT '受益人类型（MAIN-主受益人，SECONDARY-次受益人）',
  `beneficiary_id` varchar(64) DEFAULT NULL COMMENT '受益人ID（可以是用户ID、合作方ID等）',
  `share_ratio` decimal(5,4) DEFAULT NULL COMMENT '分成比例（0-1之间的小数，如0.7000表示70%）',
  `remark` text DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `is_deleted` varchar(1) DEFAULT '0' COMMENT '是否删除(0-未删除,1-已删除)',
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_beneficiary_type` (`beneficiary_type`),
  KEY `idx_beneficiary_id` (`beneficiary_id`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_is_deleted` (`is_deleted`),
  CONSTRAINT `fk_beneficiary_contract` FOREIGN KEY (`contract_id`) REFERENCES `recycle_contract` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='回收合同受益人表';

-- =============================================
-- 创建复合索引优化查询性能
-- =============================================

-- 合同ID和受益人类型复合索引（用于查询某个合同的主受益人或次受益人）
CREATE INDEX `idx_contract_type` ON `recycle_contract_beneficiary` (`contract_id`, `beneficiary_type`);

-- 受益人ID和类型复合索引（用于查询某个受益人在哪些合同中）
CREATE INDEX `idx_beneficiary_id_type` ON `recycle_contract_beneficiary` (`beneficiary_id`, `beneficiary_type`);

