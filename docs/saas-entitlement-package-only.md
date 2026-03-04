# eat 套餐授权（Entitlement：仅租户+套餐）

## 1. 目标

- 平台侧定义：套餐（`sys_package`）→ 绑定权限点（`sys_package_permission`）
- 平台给租户开通：租户套餐（`sys_tenant_package`），可配置 `start_time/end_time`（到期失效）
- 运行时生效：
  - 用户最终权限 = 角色权限 ∩ 租户已开通套餐对应的权限并集
  - 租户 SUPER_ADMIN 也只能拥有“该租户已开通套餐的全部权限”
- 本版本不再包含“模块/URL 前缀拦截”，所有控制基于权限点（菜单/按钮/API 的 `@PermissionRequired`）

## 2. 数据库迁移

执行脚本：

- `sql/2026_03_03_entitlement_package_only.sql`

表结构：

- 平台全局表（不做 tenant 过滤）
  - `sys_package`
  - `sys_package_permission`
- 租户表（tenant-scoped，会被 TenantLine 自动拼 `tenant_id`）
  - `sys_tenant_package`

种子数据：

- 插入平台菜单权限码：`menu:/admin/package`
- 创建基础套餐：`pkg_basic(code=basic)`
- 将“除平台菜单(租户管理/套餐管理)以外”的全部权限点绑定到 `basic`（避免升级后租户侧菜单全消失；后续可逐步拆分套餐权限）
- 默认租户 `t1` 开通 `basic`（永久）

## 3. 后端改造点位

### 3.1 多租户忽略表补充

`hs_api/src/main/java/com/tutu/api/config/mybatis/MybatisPlusConfig.java`

ignoreTable 增加：

- `sys_package`
- `sys_package_permission`

### 3.2 租户授权快照计算（带 60s TTL 缓存）

`hs_system/src/main/java/com/tutu/system/service/entitlement/TenantEntitlementService.java`

- active packages（按 start/end/status 判断）
- allowedPermissionIds = union(`sys_package_permission.permission_id`)

### 3.3 Sa-Token 权限链路：交集计算

`hs_api/src/main/java/com/tutu/api/config/permission/PermissionCheckConfig.java`

规则：

- 平台管理员（固定 `userId=1`）：返回全量权限码（不受套餐限制）
- 租户 SUPER_ADMIN：返回该租户 entitlement 允许的全部权限码
- 普通用户：`rolePerms ∩ allowedPermissionIds`

### 3.4 角色授权上限校验（防抓包绕过）

`hs_api/src/main/java/com/tutu/api/controller/admin/user/RoleController.java`

在调用 `adRoleService.assignPermissions(...)` 前校验：

- `permissionIds ⊆ allowedPermissionIds`

### 3.5 平台接口

- 套餐管理：`/admin/package/*`
  - 绑定权限：`GET/PUT /admin/package/{id}/permission-ids`
- 租户订阅配置：
  - `GET /admin/tenant/{tenantId}/subscription/packages`
  - `POST /admin/tenant/{tenantId}/subscription/grant-package`（支持 end_time）
  - `PUT /admin/tenant/{tenantId}/subscription/package/{packageId}/status`
  - `GET /admin/tenant/{tenantId}/subscription/entitlement-snapshot`

## 4. 前端（admin-v3）页面

路由：

- `/admin/package`：套餐管理（平台）
- `/admin/tenant`：租户管理（平台）增强了“订阅配置”入口

代码位置：

- `web/admin-v3/src/views/admin/package/index.vue`
- `web/admin-v3/src/views/admin/tenant/index.vue`

API：

- `web/admin-v3/src/api/admin/package.js`
- `web/admin-v3/src/api/admin/tenantSubscription.js`

## 5. 验收建议

1) 平台 admin（userId=1）可见菜单：租户管理/套餐管理；租户 SUPER_ADMIN 不可见  
2) 给租户开通某套餐后：相关 `menu:*` 权限可见，接口上的 `@PermissionRequired` 放行  
3) 将 `end_time` 设为过去：相关 `menu:*` 权限消失；接口上的 `@PermissionRequired` 拒绝  
4) 租户角色授权：尝试绑定未开通套餐的 permissionId → 后端拒绝  

