-- 后台用户体系：表名最终统一（建议统一为 snake_case）
--
-- 目标：
-- 1) 统一后台用户体系相关表名为：
--    ad_user / ad_role / ad_permission / ad_department / ad_user_role / ad_role_permission / ad_operation_log
-- 2) 与 MyBatis-Plus 默认类名映射（AdUser -> ad_user）保持一致，减少环境差异。
--
-- 注意：
-- - 本脚本默认“创建缺失表”，不会 DROP 旧表。
-- - 如果你历史库使用了 `admin_user` / `adRole` / `adPermission` / `adDepartment` / `admin_user_role` / `role_permission` / `operation_log`，
--   请在执行前先备份数据，然后按本脚本末尾“可选：表重命名”部分手工选择性执行。

-- 1) ad_user
CREATE TABLE IF NOT EXISTS `ad_user` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '登录账号',
  `password` varchar(255) NOT NULL COMMENT '账号密码',
  `status` varchar(50) DEFAULT 'use' COMMENT '状态(use/disable)',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称/姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `dept_id` varchar(64) DEFAULT NULL COMMENT '部门ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ad_user_username` (`username`),
  KEY `idx_ad_user_phone` (`phone`),
  KEY `idx_ad_user_dept_id` (`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台用户表';

-- 2) ad_role
CREATE TABLE IF NOT EXISTS `ad_role` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `name` varchar(50) NOT NULL COMMENT '角色名',
  `code` varchar(50) NOT NULL COMMENT '角色编码',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_ad_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台角色表';

-- 3) ad_permission
CREATE TABLE IF NOT EXISTS `ad_permission` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '权限名称',
  `code` varchar(100) NOT NULL COMMENT '权限编码(perm_code/menu_code)',
  `type` int DEFAULT 2 COMMENT '权限类型：1-菜单，2-按钮/接口',
  `parent_id` varchar(64) DEFAULT '0' COMMENT '父权限ID',
  `path` varchar(255) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(100) DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` int DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_ad_permission_code` (`code`),
  KEY `idx_ad_permission_parent_id` (`parent_id`),
  KEY `idx_ad_permission_type` (`type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台权限表';

-- 4) ad_department
CREATE TABLE IF NOT EXISTS `ad_department` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `name` varchar(100) NOT NULL COMMENT '部门名称',
  `code` varchar(100) NOT NULL COMMENT '部门编码',
  `parent_id` varchar(64) DEFAULT '0' COMMENT '父部门ID',
  `sort_order` int DEFAULT 0 COMMENT '排序',
  `status` int DEFAULT 1 COMMENT '状态：1-启用，0-禁用',
  `remark` varchar(255) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_ad_department_code` (`code`),
  KEY `idx_ad_department_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台部门表';

-- 5) ad_user_role
CREATE TABLE IF NOT EXISTS `ad_user_role` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID',
  `role_id` varchar(64) NOT NULL COMMENT '角色ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_ad_user_role_user_id` (`user_id`),
  KEY `idx_ad_user_role_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台用户-角色关联表';

-- 6) ad_role_permission
CREATE TABLE IF NOT EXISTS `ad_role_permission` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `role_id` varchar(64) NOT NULL COMMENT '角色ID',
  `permission_id` varchar(64) NOT NULL COMMENT '权限ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_ad_role_permission_role_id` (`role_id`),
  KEY `idx_ad_role_permission_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台角色-权限关联表';

-- 7) ad_operation_log（若你准备继续使用 AdOperationLog 作为后台操作日志表）
CREATE TABLE IF NOT EXISTS `ad_operation_log` (
  `id` varchar(64) NOT NULL COMMENT '主键ID',
  `user_id` varchar(64) DEFAULT NULL COMMENT '操作用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '操作用户名',
  `operation_type` varchar(20) NOT NULL COMMENT '操作类型',
  `operation_name` varchar(100) NOT NULL COMMENT '操作名称',
  `method_name` varchar(200) DEFAULT NULL COMMENT '方法名',
  `request_method` varchar(10) DEFAULT NULL COMMENT '请求方式',
  `request_url` varchar(500) DEFAULT NULL COMMENT '请求URL',
  `request_params` text COMMENT '请求参数',
  `response_data` text COMMENT '响应数据',
  `ip_address` varchar(50) DEFAULT NULL COMMENT 'IP地址',
  `user_agent` varchar(1000) DEFAULT NULL COMMENT 'User-Agent',
  `execution_time` bigint DEFAULT NULL COMMENT '执行时间(毫秒)',
  `status` int DEFAULT 1 COMMENT '执行状态：1-成功，0-失败',
  `error_message` text COMMENT '错误信息',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新人',
  `is_deleted` varchar(1) NOT NULL DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  KEY `idx_ad_operation_log_user_id` (`user_id`),
  KEY `idx_ad_operation_log_operation_type` (`operation_type`),
  KEY `idx_ad_operation_log_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台操作日志表';

-- --------------------------------------------
-- 可选：表重命名（仅当你历史表名是 camelCase/旧命名时使用）
-- 执行前务必备份，并确认没有同名表冲突。
-- --------------------------------------------
-- RENAME TABLE `admin_user` TO `ad_user`;
-- RENAME TABLE `adRole` TO `ad_role`;
-- RENAME TABLE `adPermission` TO `ad_permission`;
-- RENAME TABLE `adDepartment` TO `ad_department`;
-- RENAME TABLE `admin_user_role` TO `ad_user_role`;
-- RENAME TABLE `role_permission` TO `ad_role_permission`;
-- RENAME TABLE `operation_log` TO `ad_operation_log`;

