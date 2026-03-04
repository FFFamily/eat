-- [DEPRECATED] SaaS：按模块/套餐的租户授权（旧方案，已在 2026-03-03 切换为“仅租户+套餐”）
-- 请改用：sql/2026_03_03_entitlement_package_only.sql
--
-- 说明：
-- - 本脚本新增“平台全局配置表”（不做 tenant_id 过滤）与“租户订阅表”（tenant-scoped）。
-- - 租户订阅表需要在多租户 TenantLine 已启用的前提下工作（tenant_id 行级隔离）。
-- - 种子数据策略：默认创建一个基础模块(base)与基础套餐(basic)，并将默认租户 t1 开通基础套餐。
--
-- 注意：本脚本尽量使用幂等写法（CREATE TABLE IF NOT EXISTS / ON DUPLICATE KEY UPDATE）。

-- =========================
-- 1) 平台全局表（不做租户过滤）
-- =========================

CREATE TABLE IF NOT EXISTS `sys_module` (
  `id` varchar(64) NOT NULL COMMENT '模块ID',
  `code` varchar(64) NOT NULL COMMENT '模块编码(唯一)',
  `name` varchar(128) NOT NULL COMMENT '模块名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0停用)',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `route_prefixes` text DEFAULT NULL COMMENT 'URL前缀(JSON数组)，用于模块拦截',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_module_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块定义(平台)';

CREATE TABLE IF NOT EXISTS `sys_module_permission` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `module_id` varchar(64) NOT NULL COMMENT '模块ID',
  `permission_id` varchar(64) NOT NULL COMMENT '权限ID(ad_permission.id)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_module_permission` (`module_id`, `permission_id`, `is_deleted`),
  KEY `idx_smp_module` (`module_id`),
  KEY `idx_smp_permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='模块-权限点绑定(平台)';

CREATE TABLE IF NOT EXISTS `sys_package` (
  `id` varchar(64) NOT NULL COMMENT '套餐ID',
  `code` varchar(64) NOT NULL COMMENT '套餐编码(唯一)',
  `name` varchar(128) NOT NULL COMMENT '套餐名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0停用)',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `duration_days` int DEFAULT NULL COMMENT '默认时长(天)，为空表示由开通时指定/或永久',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_package_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐定义(平台)';

CREATE TABLE IF NOT EXISTS `sys_package_module` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `package_id` varchar(64) NOT NULL COMMENT '套餐ID',
  `module_id` varchar(64) NOT NULL COMMENT '模块ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_package_module` (`package_id`, `module_id`, `is_deleted`),
  KEY `idx_spm_package` (`package_id`),
  KEY `idx_spm_module` (`module_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐-模块绑定(平台)';

-- =========================
-- 2) 租户订阅表（tenant-scoped）
-- =========================

CREATE TABLE IF NOT EXISTS `sys_tenant_package` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `package_id` varchar(64) NOT NULL COMMENT '套餐ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间(为空表示立即生效)',
  `end_time` datetime DEFAULT NULL COMMENT '到期时间(为空表示永久)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0停用)',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_tenant_package` (`tenant_id`, `package_id`, `is_deleted`),
  KEY `idx_stp_tenant_end` (`tenant_id`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户-套餐订阅(租户表)';

CREATE TABLE IF NOT EXISTS `sys_tenant_module` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID',
  `module_id` varchar(64) NOT NULL COMMENT '模块ID',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间(为空表示立即生效)',
  `end_time` datetime DEFAULT NULL COMMENT '到期时间(为空表示永久)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0停用)',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_tenant_module` (`tenant_id`, `module_id`, `is_deleted`),
  KEY `idx_stm_tenant_end` (`tenant_id`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户-模块订阅(租户表)';

-- =========================
-- 3) 种子数据
-- =========================

-- 3.1 平台菜单权限码（用于前端路由展示；平台管理员 userId=1 默认可见）
INSERT INTO ad_permission (
  id, name, code, type, parent_id, sort_order, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('perm_menu_admin_module', '模块管理(平台)', 'menu:/admin/module', 1, '0', 0, 1, '平台菜单；仅平台管理员可访问接口', NOW(), NOW(), 'system', 'system', '0'),
  ('perm_menu_admin_package', '套餐管理(平台)', 'menu:/admin/package', 1, '0', 0, 1, '平台菜单；仅平台管理员可访问接口', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  code = VALUES(code),
  type = VALUES(type),
  parent_id = VALUES(parent_id),
  sort_order = VALUES(sort_order),
  status = VALUES(status),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3.2 基础模块/套餐（默认把“租户侧全部权限点”先归入 base，避免升级后菜单全消失）
INSERT INTO sys_module (
  id, code, name, status, sort_order, route_prefixes, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('mod_base', 'base', '基础模块', 1, 0, '[]', '默认兜底模块：用于承载租户侧基础权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  status = VALUES(status),
  sort_order = VALUES(sort_order),
  route_prefixes = VALUES(route_prefixes),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

INSERT INTO sys_package (
  id, code, name, status, sort_order, duration_days, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('pkg_basic', 'basic', '基础套餐', 1, 0, NULL, '默认套餐：绑定基础模块', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  status = VALUES(status),
  sort_order = VALUES(sort_order),
  duration_days = VALUES(duration_days),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

INSERT INTO sys_package_module (
  id, package_id, module_id, create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('spm_basic_base', 'pkg_basic', 'mod_base', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  package_id = VALUES(package_id),
  module_id = VALUES(module_id),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3.3 将“除平台菜单(/admin/*)以外”的权限点全部绑定到 base 模块（便于你后续在页面里逐步拆分模块）
-- 说明：本段使用 INSERT ... SELECT，避免手工维护大量权限点。
INSERT INTO sys_module_permission (id, module_id, permission_id, create_time, update_time, create_by, update_by, is_deleted)
SELECT
  UUID(), 'mod_base', p.id, NOW(), NOW(), 'system', 'system', '0'
FROM ad_permission p
WHERE p.is_deleted = '0'
  AND p.status = 1
  AND p.code NOT IN (
    'menu:/admin/tenant', 'menu:/admin/module', 'menu:/admin/package',
    'menu:admin/tenant', 'menu:admin/module', 'menu:admin/package'
  )
ON DUPLICATE KEY UPDATE
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3.4 给默认租户 t1 开通基础套餐（永久：end_time=NULL）
-- 注意：tenant-scoped 表在 TenantLine 环境下由后端写入 tenant_id，纯 SQL 场景需显式指定 tenant_id。
INSERT INTO sys_tenant_package (
  id, tenant_id, package_id, start_time, end_time, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('stp_t1_basic', 't1', 'pkg_basic', NOW(), NULL, 1, '默认租户基础套餐(永久)', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  status = VALUES(status),
  end_time = VALUES(end_time),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';
