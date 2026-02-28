-- 后台用户体系：登录日志表

DROP TABLE IF EXISTS `sys_login_log`;
CREATE TABLE `sys_login_log` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `username` varchar(100) DEFAULT NULL COMMENT '登录账号',
  `login_type` varchar(20) DEFAULT NULL COMMENT '登录端类型(ad/wx/...)',
  `success` tinyint NOT NULL DEFAULT 0 COMMENT '是否成功(1成功/0失败)',
  `reason` varchar(255) DEFAULT NULL COMMENT '失败原因/说明',
  `ip` varchar(64) DEFAULT NULL COMMENT 'IP',
  `ua` varchar(512) DEFAULT NULL COMMENT 'User-Agent',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_sys_login_log_username` (`username`),
  KEY `idx_sys_login_log_create_time` (`create_time`),
  KEY `idx_sys_login_log_success` (`success`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='登录日志';

