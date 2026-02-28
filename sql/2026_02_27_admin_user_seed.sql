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

-- 1) 插入 admin 用户（不存在才插入）
INSERT INTO ad_user (
  id, username, password, status, nickname, phone, avatar, dept_id,
  create_time, update_time, create_by, update_by, is_deleted
)
SELECT
  '1', 'admin', '96e79218965eb72c92a549dd5a330112', 'use', '管理员', NULL, NULL, NULL,
  NOW(), NOW(), 'system', 'system', '0'
WHERE NOT EXISTS (
  SELECT 1 FROM ad_user WHERE username = 'admin' AND is_deleted = '0'
);

-- 2) 确保 SUPER_ADMIN 角色存在（不存在才插入）
INSERT INTO ad_role (id, name, code, remark, create_time, update_time, create_by, update_by, is_deleted)
SELECT 'role_super_admin', '超级管理员', 'SUPER_ADMIN', '内置角色', NOW(), NOW(), 'system', 'system', '0'
WHERE NOT EXISTS (SELECT 1 FROM ad_role WHERE code = 'SUPER_ADMIN' AND is_deleted = '0');

-- 3) 绑定 admin -> SUPER_ADMIN（不存在才插入）
INSERT INTO ad_user_role (id, user_id, role_id, create_time, update_time, create_by, update_by, is_deleted)
SELECT
  'ur_admin_super_admin', u.id, r.id, NOW(), NOW(), 'system', 'system', '0'
FROM ad_user u
JOIN ad_role r ON r.code = 'SUPER_ADMIN' AND r.is_deleted = '0'
WHERE u.username = 'admin'
  AND u.is_deleted = '0'
  AND NOT EXISTS (
    SELECT 1 FROM ad_user_role ur
    WHERE ur.user_id = u.id AND ur.role_id = r.id AND ur.is_deleted = '0'
  );

