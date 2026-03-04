-- Add platform tenant management menu permission (non-destructive).
-- Frontend route permission code convention: `menu:${path}`.
--
-- Note: This does NOT bind to any role on purpose. Platform admin (userId=1) loads all permissions.

INSERT INTO ad_permission (
  id, name, code, type, parent_id, sort_order, status, remark,
  create_time, update_time, create_by, update_by, is_deleted
) VALUES
  ('perm_menu_admin_tenant', '租户管理(平台)', 'menu:/admin/tenant', 1, '0', 0, 1, '平台菜单；仅平台管理员可访问接口', NOW(), NOW(), 'system', 'system', '0')
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

