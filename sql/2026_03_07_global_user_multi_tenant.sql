-- SaaS：全局用户 + 多租户成员关系 + 登录后选租户（DB 变更）
--
-- 说明：
-- - 本脚本仅包含关键表结构变更（不包含“删除历史数据”）。
-- - 请先备份数据库。
-- - MySQL 对部分 ALTER 不支持 IF EXISTS，请按实际情况手工执行/调整。

-- =========================
-- 1) ad_user：从租户用户改为全局用户
-- =========================
-- 目标：
-- - 删除 ad_user.tenant_id（以及 dept_id 如你决定把部门迁移到成员关系表）
-- - username 全局唯一

-- 示例：删除 tenant_id（索引名请按实际库修正）
-- ALTER TABLE `ad_user` DROP INDEX `idx_ad_user_tenant_id`;
-- ALTER TABLE `ad_user` DROP COLUMN `tenant_id`;

-- 示例：删除 dept_id（如果你已把部门迁移到 ad_user_tenant.dept_id）
-- ALTER TABLE `ad_user` DROP COLUMN `dept_id`;

-- 确保 username 全局唯一（索引名请按实际库修正）
-- ALTER TABLE `ad_user` DROP INDEX `uk_ad_user_tenant_username`;
-- ALTER TABLE `ad_user` ADD UNIQUE KEY `uk_ad_user_username` (`username`);

-- =========================
-- 2) 用户-租户成员关系表（全局表，不参与 TenantLine 过滤）
-- =========================
CREATE TABLE IF NOT EXISTS `ad_user_tenant` (
  `id` varchar(64) NOT NULL COMMENT 'ID',
  `user_id` varchar(64) NOT NULL COMMENT '用户ID(ad_user.id)',
  `tenant_id` varchar(64) NOT NULL COMMENT '租户ID(sys_tenant.id)',
  `dept_id` varchar(64) DEFAULT NULL COMMENT '部门ID(ad_department.id)',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0禁用)',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_ad_user_tenant` (`user_id`, `tenant_id`, `is_deleted`),
  KEY `idx_aut_user` (`user_id`),
  KEY `idx_aut_tenant` (`tenant_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户-租户成员关系(全局)';

-- =========================
-- 3) 推荐：ad_user_role 唯一键收紧（按租户维度）
-- =========================
-- ALTER TABLE `ad_user_role`
--   ADD UNIQUE KEY `uk_ad_user_role_tenant_user_role` (`tenant_id`,`user_id`,`role_id`,`is_deleted`);

