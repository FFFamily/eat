-- 账号类型管理表
CREATE TABLE `sys_account_type` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  
  -- ========== 基本信息 ==========
  `code` varchar(50) NOT NULL COMMENT '编号',
  `type_name` varchar(100) NOT NULL COMMENT '账号类型名称',
  `description` varchar(500) DEFAULT NULL COMMENT '账号类型描述',
  
  -- ========== 管理信息 ==========
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态：0-禁用，1-启用',
  
  -- ========== 基础字段 ==========
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` char(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除标识：0-未删除，1-已删除',
  
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_type_name` (`type_name`),
  KEY `idx_status` (`status`),
  KEY `idx_sort_order` (`sort_order`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='账号类型管理表';

-- 创建复合索引优化查询性能
CREATE INDEX `idx_account_type_status_sort` ON `sys_account_type` (`status`, `sort_order`);
CREATE INDEX `idx_account_type_create_time` ON `sys_account_type` (`create_time`, `sort_order`);

-- 插入初始数据
INSERT INTO `sys_account_type` (`id`, `code`, `type_name`, `description`, `sort_order`, `status`) VALUES 
('1', 'ADMIN', '管理员账号', '系统管理员账号，拥有最高权限', 1, 1),
('2', 'USER', '普通用户账号', '普通用户账号，基础功能权限', 2, 1),
('3', 'GUEST', '访客账号', '访客账号，只读权限', 3, 1),
('4', 'OPERATOR', '操作员账号', '操作员账号，业务操作权限', 4, 1),
('5', 'AUDITOR', '审计员账号', '审计员账号，审计查看权限', 5, 1); 