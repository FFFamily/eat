-- 合同日志表
CREATE TABLE `sys_contract_log` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  
  -- ========== 合同信息 ==========
  `contract_id` varchar(64) NOT NULL COMMENT '合同ID',
  `contract_no` varchar(100) NOT NULL COMMENT '合同编号',
  
  -- ========== 操作信息 ==========
  `operation_type` varchar(50) NOT NULL COMMENT '操作类型：UPDATE-更新，CREATE-创建，DELETE-删除，APPROVE-审核，REJECT-驳回，SIGN-签署，ACTIVATE-生效，TERMINATE-终止',
  `operator_id` varchar(64) NOT NULL COMMENT '操作人ID',
  `operator_name` varchar(100) NOT NULL COMMENT '操作人姓名',
  `operation_time` datetime NOT NULL COMMENT '操作时间',
  
  -- ========== 操作详情 ==========
  `operation_info` json DEFAULT NULL COMMENT '操作信息数组，格式：[{"field":"字段名","fieldLabel":"字段中文名称","oldValue":"变更前值","newValue":"变更后值"}]',
  
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
  KEY `idx_operation_time` (`operation_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='合同日志表';

-- 创建复合索引优化查询性能
CREATE INDEX `idx_contract_log_composite` ON `sys_contract_log` (`contract_id`, `operation_time`);
CREATE INDEX `idx_contract_log_operator` ON `sys_contract_log` (`operator_id`, `operation_time`);
CREATE INDEX `idx_contract_log_type_time` ON `sys_contract_log` (`operation_type`, `operation_time`);

-- 插入示例数据（可选）
INSERT INTO `sys_contract_log` (`id`, `contract_id`, `contract_no`, `operation_type`, `operator_id`, `operator_name`, `operation_time`, `operation_info`) VALUES 
('1', 'contract001', 'HT2024001', 'CREATE', 'user001', '张三', NOW(), '[{"field":"contractName","fieldLabel":"合同名称","oldValue":"","newValue":"测试合同"}]'),
('2', 'contract001', 'HT2024001', 'UPDATE', 'user002', '李四', NOW(), '[{"field":"contractAmount","fieldLabel":"合同金额","oldValue":"10000","newValue":"15000"}]'); 