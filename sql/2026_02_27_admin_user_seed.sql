-- 后台用户体系：默认管理员账号 seed（ad_user + 绑定 SUPER_ADMIN）
--
-- 默认账号：
--   username: admin
--   password: 123456
--
-- 注意：
-- - 这里用 MD5('123456') = 96e79218965eb72c92a549dd5a330112 作为初始密码存储，
--   项目代码支持旧 MD5 校验，并会在首次登录后自动升级为 BCrypt（见 PasswordUtil / LoginService）。
-- - 执行前请确认你已使用统一表名（ad_user / ad_role / ad_user_role）。

-- 1) 插入 admin 用户（不存在才插入）（全局用户：不带 tenant_id）
INSERT INTO ad_user (
  id, username, password, status, nickname, phone, avatar,
  create_time, update_time, create_by, update_by, is_deleted
)
SELECT
  '1', 'admin', '96e79218965eb72c92a549dd5a330112', 'use', '管理员', NULL, NULL,
  NOW(), NOW(), 'system', 'system', '0'
WHERE NOT EXISTS (
  SELECT 1 FROM ad_user WHERE username = 'admin' AND is_deleted = '0'
);

-- 2) 绑定 admin -> 默认租户 t1（成员关系）
-- 前置：已存在 sys_tenant(t1, default)
INSERT INTO ad_user_tenant (
  id, user_id, tenant_id, dept_id, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
)
SELECT
  'aut_admin_t1', '1', 't1', NULL, 1, '默认管理员加入默认租户',
  NOW(), NOW(), 'system', 'system', '0'
WHERE NOT EXISTS (
  SELECT 1 FROM ad_user_tenant WHERE user_id = '1' AND tenant_id = 't1' AND is_deleted = '0'
);

-- 3) 确保 SUPER_ADMIN 角色存在（默认租户 t1，tenant-scoped）
INSERT INTO ad_role (id, tenant_id, name, code, remark, create_time, update_time, create_by, update_by, is_deleted)
SELECT 'role_super_admin_t1', 't1', '超级管理员', 'SUPER_ADMIN', '内置角色', NOW(), NOW(), 'system', 'system', '0'
WHERE NOT EXISTS (SELECT 1 FROM ad_role WHERE tenant_id = 't1' AND code = 'SUPER_ADMIN' AND is_deleted = '0');

-- 4) 绑定 admin -> SUPER_ADMIN（默认租户 t1，tenant-scoped）
INSERT INTO ad_user_role (id, tenant_id, user_id, role_id, create_time, update_time, create_by, update_by, is_deleted)
SELECT
  'ur_admin_super_admin_t1', 't1', u.id, r.id, NOW(), NOW(), 'system', 'system', '0'
FROM ad_user u
JOIN ad_role r ON r.tenant_id = 't1' AND r.code = 'SUPER_ADMIN' AND r.is_deleted = '0'
WHERE u.username = 'admin'
  AND u.is_deleted = '0'
  AND NOT EXISTS (
    SELECT 1 FROM ad_user_role ur
    WHERE ur.tenant_id = 't1' AND ur.user_id = u.id AND ur.role_id = r.id AND ur.is_deleted = '0'
  );
