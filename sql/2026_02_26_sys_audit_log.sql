-- 后台用户体系：审计日志表

DROP TABLE IF EXISTS `sys_audit_log`;
CREATE TABLE `sys_audit_log` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `actor_user_id` varchar(64) DEFAULT NULL COMMENT '操作者用户ID',
  `action` varchar(100) NOT NULL COMMENT '动作标识（如 user.create / role.bind_permissions）',
  `target_type` varchar(50) DEFAULT NULL COMMENT '目标类型（user/role/permission/...)',
  `target_id` varchar(64) DEFAULT NULL COMMENT '目标ID',
  `detail_json` text DEFAULT NULL COMMENT '详情(JSON)',
  `result` tinyint NOT NULL DEFAULT 1 COMMENT '结果(1成功/0失败)',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP',
  `ua` varchar(512) DEFAULT NULL COMMENT 'User-Agent',
  `request_id` varchar(64) DEFAULT NULL COMMENT '请求ID/链路ID',
  `error_message` varchar(255) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_audit_log_actor` (`actor_user_id`),
  KEY `idx_sys_audit_log_action` (`action`),
  KEY `idx_sys_audit_log_target` (`target_type`, `target_id`),
  KEY `idx_sys_audit_log_result` (`result`),
  KEY `idx_sys_audit_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审计日志';

