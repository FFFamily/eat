-- 系统公告表（租户隔离）

CREATE TABLE IF NOT EXISTS `sys_announcement` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text COMMENT '公告内容（纯文本）',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：1-发布，0-下线',
  `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_announcement_tenant_status_time` (`tenant_id`, `status`, `create_time`),
  KEY `idx_announcement_tenant_title` (`tenant_id`, `title`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统公告表';
