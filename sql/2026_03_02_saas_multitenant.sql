-- SaaS 多租户改造（共享库共享表 + tenant_id 行级隔离）
-- 说明：
-- 1) 请先备份数据库；
-- 2) 下列 ALTER 请按你实际库表情况选择性执行（MySQL 不支持 ALTER TABLE ... ADD COLUMN IF NOT EXISTS）；
-- 3) 本脚本默认把存量数据归属到默认租户 tenant_id='t1'（tenant_code='default'）。

-- =========================
-- 1) 全局租户表（不做租户过滤）
-- =========================
CREATE TABLE IF NOT EXISTS `sys_tenant` (
  `id` varchar(64) NOT NULL COMMENT '租户ID',
  `code` varchar(64) NOT NULL COMMENT '租户编码(前端传 X-Tenant-Code)',
  `name` varchar(128) NOT NULL COMMENT '租户名称',
  `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态(1启用/0禁用)',
  `remark` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `create_by` varchar(64) DEFAULT NULL,
  `update_by` varchar(64) DEFAULT NULL,
  `is_deleted` varchar(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_sys_tenant_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

INSERT INTO sys_tenant(id, code, name, status, create_time, update_time, is_deleted)
VALUES ('t1', 'default', '默认租户', 1, NOW(), NOW(), '0')
ON DUPLICATE KEY UPDATE code=code;

-- =========================
-- 2) 核心表：tenant_id + 唯一键调整
-- =========================

-- 2.1 account（C端/业务账号）
ALTER TABLE `account`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_account_tenant_id` (`tenant_id`);
-- 旧唯一键改为租户内唯一（索引名请按实际库修正）
-- ALTER TABLE `account` DROP INDEX `username`;
-- ALTER TABLE `account` ADD UNIQUE KEY `uk_account_tenant_username` (`tenant_id`,`username`);

-- 2.2 ad_user（管理端账号）
ALTER TABLE `ad_user`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_ad_user_tenant_id` (`tenant_id`);
-- ALTER TABLE `ad_user` DROP INDEX `uk_ad_user_username`;
-- ALTER TABLE `ad_user` ADD UNIQUE KEY `uk_ad_user_tenant_username` (`tenant_id`,`username`);

-- 2.3 RBAC 关联表
ALTER TABLE `ad_role`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_ad_role_tenant_id` (`tenant_id`);
-- ALTER TABLE `ad_role` ADD UNIQUE KEY `uk_ad_role_tenant_code` (`tenant_id`,`code`);

ALTER TABLE `ad_department`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_ad_department_tenant_id` (`tenant_id`);
-- ALTER TABLE `ad_department` DROP INDEX `uk_ad_department_code`;
-- ALTER TABLE `ad_department` ADD UNIQUE KEY `uk_ad_department_tenant_code` (`tenant_id`,`code`);

ALTER TABLE `ad_user_role`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_ad_user_role_tenant_id` (`tenant_id`);

ALTER TABLE `ad_role_permission`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_ad_role_permission_tenant_id` (`tenant_id`);

ALTER TABLE `ad_operation_log`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_ad_operation_log_tenant_id` (`tenant_id`);

-- 2.4 系统文件/消息/审计/登录日志（强建议租户隔离）
ALTER TABLE `sys_file`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_sys_file_tenant_id` (`tenant_id`);

ALTER TABLE `sys_message`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_sys_message_tenant_id` (`tenant_id`);

ALTER TABLE `sys_login_log`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_sys_login_log_tenant_id` (`tenant_id`);

ALTER TABLE `sys_audit_log`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID',
  ADD KEY `idx_sys_audit_log_tenant_id` (`tenant_id`);

-- =========================
-- 3) 账号编号序列（按租户隔离）
-- =========================
ALTER TABLE `account_username_seq`
  ADD COLUMN `tenant_id` varchar(64) NOT NULL DEFAULT 't1' COMMENT '租户ID';
ALTER TABLE `account_username_seq`
  DROP PRIMARY KEY,
  ADD PRIMARY KEY (`tenant_id`, `account_type_id`);

-- =========================
-- 4) 其余业务表（示例：请按模块补齐）
-- =========================
-- 库存：inventory/warehouse/inventory_in/inventory_out/... 等
-- 租赁：lease_cart/lease_order/... 等
-- 回收：recycle_order/recycle_invoice/... 等
-- 发票：invoice/invoice_head 等
-- 积分：point_* / account_point_* 等
-- 饮食：food/food_type/... 等

