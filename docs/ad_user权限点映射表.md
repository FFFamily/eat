# ad_user 后台接口 -> 权限点（perm_code）映射表

> 目的：把“接口”和“权限点”一一对齐，便于做权限点 seed、角色授权、前端按钮显隐。
>
> 说明：
> - 本项目后端使用 `@PermissionRequired("perm_code")` 标注接口权限；
> - 超级管理员放行策略：
>   - 固定内置管理员：`userId = 1`（`AdminConstant.ADMIN_ID`）默认拥有全部权限；
>   - 角色码放行：拥有 `SUPER_ADMIN/ADMIN` 角色默认放行；
> - 本表仅覆盖后台用户体系相关接口（`/admin/**` + `/ad/auth/**`）。

## 1. 认证 Auth（后台）

| Method | Path | perm_code | 说明 |
|---|---|---|---|
| POST | `/ad/auth/login` | （无） | 登录接口通常不做权限点校验；权限点 `auth:login` 用于菜单/按钮控制或后续扩展 |
| GET | `/ad/auth/logout` | （无） | 退出登录 |
| GET | `/ad/auth/getLoginInfo` | （无） | 当前登录信息 |
| POST | `/ad/auth/register` | （无） | 注册（如不对外开放，建议后续补 `@PermissionRequired`） |

## 2. 用户 User（后台）

Base: `/admin/user`

| Method | Path | perm_code | 说明 |
|---|---|---|---|
| GET | `/page` | `user:list` | 用户分页（支持 keyword/status/deptId） |
| GET | `/info/{id}` | `user:read` | 用户详情（返回包含 `adRoles`） |
| POST | `/create` | `user:create` | 新增用户（支持一次性带角色） |
| PUT | `/update` | `user:update` | 编辑用户（支持一次性带角色） |
| DELETE | `/delete/{id}` | `user:delete` | 删除用户 |
| DELETE | `/batch` | `user:delete` | 批量删除 |
| PUT | `/{id}/reset-password` | `user:reset_password` | 重置密码 |
| PUT | `/{id}/change-password` | （无） | 修改密码（用户自助） |
| PUT | `/changeStatus` | `user:update` | 启用/禁用 |
| PUT | `/{id}/roles` | `user:update` | 分配角色 |
| GET | `/dept/{deptId}` | `user:list` | 按部门查用户 |
| GET | `/role/{roleId}` | `user:list` | 按角色查用户 |

## 3. 角色 Role（后台）

Base: `/admin/role`

| Method | Path | perm_code | 说明 |
|---|---|---|---|
| GET | `/page` | `role:list` | 角色分页 |
| GET | `/all` | `role:list` | 全部角色 |
| GET | `/{id}` | `role:read` | 角色详情 |
| POST | `` | `role:create` | 新增角色 |
| PUT | `` | `role:update` | 更新角色 |
| DELETE | `/{id}` | `role:delete` | 删除角色（内置 ADMIN/SUPER_ADMIN 等保护） |
| DELETE | `/batch` | `role:delete` | 批量删除 |
| PUT | `/{id}/permissions` | `role:bind_permissions` | 角色绑定权限 |
| GET | `/user/{userId}` | `role:list` | 查询用户角色 |

> 备注：历史遗留的“业务用户角色分配接口”已迁移到 `/admin/user-role/**`，不属于 ad_user 后台体系主链路。

## 4. 权限 Permission（后台）

Base: `/admin/permission`

| Method | Path | perm_code | 说明 |
|---|---|---|---|
| GET | `/page` | `permission:list` | 权限分页 |
| GET | `/tree` | `permission:list` | 权限树（菜单 + 接口/按钮） |
| GET | `/{id}` | `permission:list` | 权限详情 |
| POST | `` | `permission:create` | 新增权限 |
| PUT | `` | `permission:update` | 更新权限 |
| DELETE | `/{id}` | `permission:delete` | 删除权限 |
| DELETE | `/batch` | `permission:delete` | 批量删除权限 |
| GET | `/role/{roleId}` | `permission:list` | 查询角色权限 |
| GET | `/user/{userId}` | `permission:list` | 查询用户权限 |

## 5. 部门 Department（后台）

Base: `/admin/department`

| Method | Path | perm_code | 说明 |
|---|---|---|---|
| GET | `/page` | `department:list` | 部门分页 |
| GET | `/tree` | `department:list` | 部门树 |
| GET | `/all` | `department:list` | 全部启用部门 |
| GET | `/{id}` | `department:read` | 部门详情 |
| POST | `` | `department:create` | 新增部门 |
| PUT | `` | `department:update` | 更新部门 |
| DELETE | `/{id}` | `department:delete` | 删除部门 |
| DELETE | `/batch` | `department:delete` | 批量删除部门 |

## 6. 日志与审计 Audit / Log（后台）

| Method | Path | perm_code | 说明 |
|---|---|---|---|
| GET | `/admin/login-log/page` | `audit:list` | 登录日志分页 |
| GET | `/admin/operation-log/page` | `audit:list` | 操作日志分页 |
| GET | `/admin/operation-log/{id}` | `audit:list` | 操作日志详情 |
| GET | `/admin/operation-log/user/{userId}` | `audit:list` | 用户操作日志 |
| GET | `/admin/operation-log/type/{operationType}` | `audit:list` | 类型操作日志 |
| GET | `/admin/operation-log/statistics` | `audit:list` | 操作统计 |
| DELETE | `/admin/operation-log/{id}` | `audit:list` | 删除操作日志 |
| DELETE | `/admin/operation-log/batch` | `audit:list` | 批量删除操作日志 |
| DELETE | `/admin/operation-log/clean` | `audit:list` | 清理历史操作日志 |
| GET | `/admin/audit-log/page` | `audit:list` | 审计日志分页 |
