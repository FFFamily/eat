-- 合同更新日志表
CREATE TABLE `sys_contract_update_log` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  
  -- ========== 合同信息 ==========
  `contract_id` varchar(64) NOT NULL COMMENT '合同ID',
  `contract_no` varchar(100) NOT NULL COMMENT '合同编号',
  
  -- ========== 操作信息 ==========
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型：CREATE-创建，UPDATE-更新，DELETE-删除，APPROVE-审核，REJECT-驳回，SIGN-签署，ACTIVATE-生效，TERMINATE-终止',
  `operator_id` varchar(64) NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) NOT NULL COMMENT '操作人姓名',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  
  -- ========== 字段变更信息 ==========
  `field_name` varchar(100) DEFAULT NULL COMMENT '变更字段名称',
  `field_label` varchar(100) DEFAULT NULL COMMENT '字段中文名称',
  `old_value` text DEFAULT NULL COMMENT '变更前值',
  `new_value` text DEFAULT NULL COMMENT '变更后值',
  
  -- ========== 其他信息 ==========
  `change_reason` varchar(500) DEFAULT NULL COMMENT '变更原因',
  `remark` varchar(1000) DEFAULT NULL COMMENT '备注',
  `operation_ip` varchar(50) DEFAULT NULL COMMENT '操作IP地址',
  `user_agent` varchar(500) DEFAULT NULL COMMENT '用户代理（浏览器信息）',
  
  -- ========== 基础字段 ==========
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  
  PRIMARY KEY (`id`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_contract_no` (`contract_no`),
  KEY `idx_operation_type` (`operation_type`),
  KEY `idx_operator_id` (`operator_id`),
  KEY `idx_operation_time` (`operation_time`),
  KEY `idx_field_name` (`field_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同更新日志表';

-- 创建复合索引优化查询性能
CREATE INDEX `idx_contract_update_log_composite` ON `sys_contract_update_log` (`contract_id`, `operation_time`);
CREATE INDEX `idx_contract_update_log_operator` ON `sys_contract_update_log` (`operator_id`, `operation_time`);
CREATE INDEX `idx_contract_update_log_type_time` ON `sys_contract_update_log` (`operation_type`, `operation_time`); 