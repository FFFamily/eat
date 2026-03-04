-- 后台用户体系：权限点 seed（权限表插入 + 角色绑定）
--
-- 前置依赖：
-- - 表结构已统一为 snake_case（建议先执行：2026_02_27_admin_user_table_unify.sql）
-- - 权限校验使用 `ad_permission.code` 作为 perm_code
--
-- 说明：
-- - 为避免 INSERT ... SELECT / NOT EXISTS 等写法（部分环境/工具兼容性问题），本脚本全部使用 INSERT ... VALUES。
-- - 建议把它当成“权限点真源（source of truth）”：新增/修改权限点都只维护这个文件。
-- - 采用“固定主键ID + ON DUPLICATE KEY UPDATE”实现可重复执行（幂等）的效果：
--   - ad_permission.id：perm_xxx
--   - ad_role.id：role_xxx
--   - ad_role_permission.id：rp_{sa|ad}_{perm_code_下划线化}
-- - 如果你的历史库里同 code 已存在但 id 不一致，本脚本不会自动迁移/去重（需你自行清理/迁移）。

-- 0) 清理（建议每次执行都先清理，保证权限点集合“以本文件为准”）
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM ad_role_permission;
DELETE FROM ad_permission;
SET FOREIGN_KEY_CHECKS = 1;

-- 如需同时重建内置角色（可能影响 ad_user_role），再按需执行：
-- DELETE FROM ad_role WHERE code IN ('SUPER_ADMIN', 'ADMIN');
-- DELETE FROM ad_user_role;

-- 1) 插入最小权限点（type=2 表示接口/按钮权限）
INSERT INTO ad_permission (
  id, name, code, type, parent_id, sort_order, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('perm_auth_login', '登录', 'auth:login', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
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

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_user_list', '用户-列表', 'user:list', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

-- 租户管理（平台菜单）：前端路由使用 `menu:${path}` 作为菜单权限码
INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_menu_admin_tenant', '租户管理(平台)', 'menu:/admin/tenant', 1, '0', 0, 1, '平台菜单；仅平台管理员可访问接口', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_menu_admin_package', '套餐管理(平台)', 'menu:/admin/package', 1, '0', 0, 1, '平台菜单；仅平台管理员可访问接口', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_user_read', '用户-详情', 'user:read', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_user_create', '用户-新增', 'user:create', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_user_update', '用户-编辑/启禁/角色', 'user:update', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_user_delete', '用户-删除', 'user:delete', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_user_reset_password', '用户-重置密码', 'user:reset_password', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_role_list', '角色-列表', 'role:list', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_role_read', '角色-详情', 'role:read', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_role_create', '角色-新增', 'role:create', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_role_update', '角色-更新', 'role:update', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_role_delete', '角色-删除', 'role:delete', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_role_bind_permissions', '角色-绑定权限', 'role:bind_permissions', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_permission_list', '权限-列表/树', 'permission:list', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_permission_create', '权限-新增', 'permission:create', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_permission_update', '权限-更新', 'permission:update', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_permission_delete', '权限-删除', 'permission:delete', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_department_list', '部门-列表/树', 'department:list', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_department_read', '部门-详情', 'department:read', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_department_create', '部门-新增', 'department:create', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_department_update', '部门-更新', 'department:update', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_department_delete', '部门-删除', 'department:delete', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

INSERT INTO ad_permission (id, name, code, type, parent_id, sort_order, status, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('perm_audit_list', '审计/日志-列表', 'audit:list', 2, '0', 0, 1, 'API权限点', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE name=VALUES(name), code=VALUES(code), type=VALUES(type), parent_id=VALUES(parent_id), sort_order=VALUES(sort_order), status=VALUES(status), remark=VALUES(remark), update_time=NOW(), update_by=VALUES(update_by), is_deleted='0';

-- 2) 确保内置角色存在
INSERT INTO ad_role (id, name, code, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('role_super_admin', '超级管理员', 'SUPER_ADMIN', '内置角色', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  code = VALUES(code),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

INSERT INTO ad_role (id, name, code, remark, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('role_admin', '管理员', 'ADMIN', '内置角色', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  name = VALUES(name),
  code = VALUES(code),
  remark = VALUES(remark),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3) 角色绑定权限点（对 SUPER_ADMIN / ADMIN 绑定全部“最小权限点”）
-- SUPER_ADMIN
INSERT INTO ad_role_permission (id, role_id, permission_id, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('rp_sa_auth_login', 'role_super_admin', 'perm_auth_login', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_user_list', 'role_super_admin', 'perm_user_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_user_read', 'role_super_admin', 'perm_user_read', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_user_create', 'role_super_admin', 'perm_user_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_user_update', 'role_super_admin', 'perm_user_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_user_delete', 'role_super_admin', 'perm_user_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_user_reset_password', 'role_super_admin', 'perm_user_reset_password', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_role_list', 'role_super_admin', 'perm_role_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_role_read', 'role_super_admin', 'perm_role_read', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_role_create', 'role_super_admin', 'perm_role_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_role_update', 'role_super_admin', 'perm_role_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_role_delete', 'role_super_admin', 'perm_role_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_role_bind_permissions', 'role_super_admin', 'perm_role_bind_permissions', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_permission_list', 'role_super_admin', 'perm_permission_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_permission_create', 'role_super_admin', 'perm_permission_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_permission_update', 'role_super_admin', 'perm_permission_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_permission_delete', 'role_super_admin', 'perm_permission_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_department_list', 'role_super_admin', 'perm_department_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_department_read', 'role_super_admin', 'perm_department_read', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_department_create', 'role_super_admin', 'perm_department_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_department_update', 'role_super_admin', 'perm_department_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_department_delete', 'role_super_admin', 'perm_department_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_audit_list', 'role_super_admin', 'perm_audit_list', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  role_id = VALUES(role_id),
  permission_id = VALUES(permission_id),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- ADMIN
INSERT INTO ad_role_permission (id, role_id, permission_id, create_time, update_time, create_by, update_by, is_deleted) VALUES
  ('rp_ad_auth_login', 'role_admin', 'perm_auth_login', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_user_list', 'role_admin', 'perm_user_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_user_read', 'role_admin', 'perm_user_read', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_user_create', 'role_admin', 'perm_user_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_user_update', 'role_admin', 'perm_user_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_user_delete', 'role_admin', 'perm_user_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_user_reset_password', 'role_admin', 'perm_user_reset_password', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_role_list', 'role_admin', 'perm_role_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_role_read', 'role_admin', 'perm_role_read', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_role_create', 'role_admin', 'perm_role_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_role_update', 'role_admin', 'perm_role_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_role_delete', 'role_admin', 'perm_role_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_role_bind_permissions', 'role_admin', 'perm_role_bind_permissions', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_permission_list', 'role_admin', 'perm_permission_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_permission_create', 'role_admin', 'perm_permission_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_permission_update', 'role_admin', 'perm_permission_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_permission_delete', 'role_admin', 'perm_permission_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_department_list', 'role_admin', 'perm_department_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_department_read', 'role_admin', 'perm_department_read', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_department_create', 'role_admin', 'perm_department_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_department_update', 'role_admin', 'perm_department_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_department_delete', 'role_admin', 'perm_department_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_audit_list', 'role_admin', 'perm_audit_list', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  role_id = VALUES(role_id),
  permission_id = VALUES(permission_id),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 4) （可选）为初始管理员账号绑定 SUPER_ADMIN / ADMIN
-- 说明：如果你依赖“角色码放行”，务必确保后台管理员拥有 SUPER_ADMIN/ADMIN 角色；
--       若仍使用固定 ADMIN_ID=1 放行，可不执行。

-- 5) 后续新增权限点怎么维护
-- - 在“1) 插入最小权限点”区域新增一条 INSERT INTO ad_permission ... VALUES ... 语句
--   - 建议 id 命名：perm_{code 中的 ':' '.' '-' 替换为 '_' }
-- - 如需让 SUPER_ADMIN / ADMIN 默认拥有该权限点：
--   - 在“3) 角色绑定权限点”SUPER_ADMIN 和 ADMIN 的 VALUES 列表中各补一条
--   - 建议 id 命名：rp_{sa|ad}_{perm_id 去掉 perm_ 前缀}
