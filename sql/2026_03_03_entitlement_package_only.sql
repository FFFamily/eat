-- SaaS：套餐授权（仅 租户 + 套餐）
--
-- 目标：
-- - 平台定义套餐 sys_package
-- - 套餐绑定权限点 sys_package_permission（ad_permission.id）
-- - 平台给租户开通套餐 sys_tenant_package（tenant_id 行级隔离），并配置 end_time（到期失效）
-- - 用户最终权限 = 角色权限 ∩ 租户已开通套餐的权限并集
--
-- 注意：
-- - 本脚本不再创建/使用“模块”相关表（sys_module/...）。
-- - 如你之前执行过模块/套餐版本脚本，可保留旧表；后端已移除相关逻辑，不再使用。

-- =========================
-- 1) 平台全局表（不做 tenant 过滤）
-- =========================

CREATE TABLE IF NOT EXISTS `sys_package` (
  `id` varchar(64) NOT NULL COMMENT '套餐ID',
  `code` varchar(64) NOT NULL COMMENT '套餐编码(唯一)',
  `name` varchar(128) NOT NULL COMMENT '套餐名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0停用)',
  `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_package_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐定义(平台)';

CREATE TABLE IF NOT EXISTS `sys_package_permission` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `package_id` varchar(64) NOT NULL COMMENT '套餐ID',
  `permission_id` varchar(64) NOT NULL COMMENT '权限ID(ad_permission.id)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_package_permission` (`package_id`, `permission_id`, `is_deleted`),
  KEY `idx_spp_package` (`package_id`),
  KEY `idx_spp_permission` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐-权限点绑定(平台)';

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

-- =========================
-- 3) 种子数据（建议）
-- =========================

-- 3.1 平台菜单权限码（用于前端路由展示；平台管理员 userId=1 默认可见）
INSERT INTO ad_permission (
  id, name, code, type, parent_id, sort_order, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
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

-- 3.2 基础套餐（默认把“租户侧全部权限点”先归入 basic，避免升级后菜单全消失）
INSERT INTO sys_package (
  id, code, name, status, sort_order, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('pkg_basic', 'basic', '基础套餐', 1, 0, '默认兜底套餐：用于承载租户侧基础权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  status = VALUES(status),
  sort_order = VALUES(sort_order),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3.3 将“除平台菜单(租户管理/套餐管理)以外”的权限点全部绑定到 basic 套餐（便于你后续在页面里逐步拆分套餐权限）
INSERT INTO sys_package_permission (id, package_id, permission_id, create_time, update_time, create_by, update_by, is_deleted)
SELECT
  UUID(), 'pkg_basic', p.id, NOW(), NOW(), 'system', 'system', '0'
FROM ad_permission p
WHERE p.is_deleted = '0'
  AND p.status = 1
  AND p.code NOT IN (
    'menu:/admin/tenant', 'menu:/admin/package',
    'menu:admin/tenant', 'menu:admin/package'
  )
ON DUPLICATE KEY UPDATE
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3.4 给默认租户 t1 开通基础套餐（永久：end_time=NULL）
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

