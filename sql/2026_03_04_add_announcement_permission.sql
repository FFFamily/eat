-- 公告管理权限点 + 内置角色绑定 + 基础套餐绑定

-- 1) 权限点
INSERT INTO ad_permission (
  id, name, code, type, parent_id, sort_order, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('perm_menu_admin_announcement', '公告管理', 'menu:/admin/announcement', 1, '0', 0, 1, '公告管理菜单', NOW(), NOW(), 'system', 'system', '0'),
  ('perm_announcement_list', '公告-列表', 'announcement:list', 2, '0', 0, 1, '公告管理API权限', NOW(), NOW(), 'system', 'system', '0'),
  ('perm_announcement_create', '公告-新增', 'announcement:create', 2, '0', 0, 1, '公告管理API权限', NOW(), NOW(), 'system', 'system', '0'),
  ('perm_announcement_update', '公告-编辑', 'announcement:update', 2, '0', 0, 1, '公告管理API权限', NOW(), NOW(), 'system', 'system', '0'),
  ('perm_announcement_delete', '公告-删除', 'announcement:delete', 2, '0', 0, 1, '公告管理API权限', NOW(), NOW(), 'system', 'system', '0'),
  ('perm_announcement_publish', '公告-发布上下线', 'announcement:publish', 2, '0', 0, 1, '公告管理API权限', NOW(), NOW(), 'system', 'system', '0')
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

-- 2) 内置角色绑定（SUPER_ADMIN）
INSERT INTO ad_role_permission (
  id, role_id, permission_id, create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('rp_sa_announcement_list', 'role_super_admin', 'perm_announcement_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_announcement_create', 'role_super_admin', 'perm_announcement_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_announcement_update', 'role_super_admin', 'perm_announcement_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_announcement_delete', 'role_super_admin', 'perm_announcement_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_sa_announcement_publish', 'role_super_admin', 'perm_announcement_publish', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  role_id = VALUES(role_id),
  permission_id = VALUES(permission_id),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 3) 内置角色绑定（ADMIN）
INSERT INTO ad_role_permission (
  id, role_id, permission_id, create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('rp_ad_announcement_list', 'role_admin', 'perm_announcement_list', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_announcement_create', 'role_admin', 'perm_announcement_create', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_announcement_update', 'role_admin', 'perm_announcement_update', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_announcement_delete', 'role_admin', 'perm_announcement_delete', NOW(), NOW(), 'system', 'system', '0'),
  ('rp_ad_announcement_publish', 'role_admin', 'perm_announcement_publish', NOW(), NOW(), 'system', 'system', '0')
ON DUPLICATE KEY UPDATE
  role_id = VALUES(role_id),
  permission_id = VALUES(permission_id),
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';

-- 4) 绑定基础套餐（若已启用套餐模型）
INSERT INTO sys_package_permission (
  id, package_id, permission_id, create_time, update_time, create_by, update_by, is_deleted
)
SELECT
  UUID(), 'pkg_basic', p.id, NOW(), NOW(), 'system', 'system', '0'
FROM ad_permission p
WHERE p.is_deleted = '0'
  AND p.code IN (
    'menu:/admin/announcement',
    'announcement:list',
    'announcement:create',
    'announcement:update',
    'announcement:delete',
    'announcement:publish'
  )
ON DUPLICATE KEY UPDATE
  update_time = NOW(),
  update_by = VALUES(update_by),
  is_deleted = '0';
