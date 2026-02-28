# ad_user 后台前端功能清单 & API 文档（对接版）

> 依据：`/Users/tujunjie/Downloads/project/eat/docs/ad_user模块升级执行方案.md` + 当前已存在的后端接口实现（`hs_api` / `hs_admin_user`）。  
> 目标：给前端提供“页面功能清单 + 调用哪些 API + 参数/返回结构 + 建议权限点”，方便后续页面生成与联调。

## 0. 全局约定

### 0.1 BaseResponse 响应包裹
所有接口统一返回：
```json
{
  "code": 200,
  "msg": "成功",
  "data": {}
}
```
`code=200` 成功；失败通常 `code=500`，错误信息在 `msg`。

### 0.2 鉴权方式（Sa-Token）
- 登录成功后返回 token 字符串
- 前端后续请求在 Header 携带：`Token-Key: <token>`
- 说明：当前后端配置 `is-read-cookie=false`，不要依赖 Cookie。

### 0.3 分页结构（MyBatis-Plus IPage）
分页接口的 `data` 通常为：
```json
{
  "records": [],
  "total": 0,
  "size": 10,
  "current": 1,
  "pages": 0
}
```

### 0.4 权限点（perm_code）说明
升级方案要求为后台接口补齐最小权限点（示例：`user:list`、`role:update`）。  
当前项目存在 `@PermissionRequired` + AOP，但 admin 接口尚未系统性标注，且 `PermissionCheckConfig` 仍为硬编码/未接 DB。  
本文档为前端预先给出“建议权限点”，便于后续做按钮级控制与接口对齐。

---

## 1. 前端页面/功能清单（建议菜单结构）

### 1.1 登录与会话
- 登录页
  - 用户名/密码登录
- 顶栏用户信息
  - 获取当前登录用户信息
  - 退出登录

### 1.2 用户管理
- 用户列表
  - 分页、关键字搜索（username/nickname）
  - 启用/禁用
  - 新增用户（设置初始密码、角色、部门）
  - 编辑用户（基本信息、角色、部门）
  - 删除/批量删除
  - 重置密码
  - 分配角色
- 用户详情
  - 基本信息 + 所属部门 + 角色列表（建议聚合返回）

### 1.3 角色管理
- 角色列表
  - 分页、关键字搜索（name/code）
  - 新增/编辑/删除/批量删除
  - 角色授权：绑定权限点（多选树/列表）
- 角色详情
  - 基本信息 + 绑定权限列表

### 1.4 权限管理
- 权限点列表
  - 分页、关键字搜索（name/code）
  - 新增/编辑/删除/批量删除
- 菜单权限树
  - 树形展示（按 `parentId`）

### 1.5 部门管理
- 部门列表
  - 分页、关键字搜索
  - 新增/编辑/删除/批量删除
- 部门树
  - 树形展示（按 `parentId`）

### 1.6 日志与审计（升级项）
- 登录日志（新增页面）
  - 登录成功/失败列表、按用户名/时间/IP筛选
- 审计日志（新增页面）
  - actor/action/target/time 范围筛选
  - 查看 detail_json
- 操作日志（已有页面）
  - 分页筛选、详情、统计、清理

---

## 2. API 文档（按模块）

## 2.1 认证 Auth（后台）

### 2.1.1 登录
- Method: `POST`
- Path: `/ad/auth/login`
- 建议权限点：`auth:login`
- Body:
```json
{ "username": "admin", "password": "123456" }
```
- Resp: `data` 为 token 字符串

### 2.1.2 退出登录
- Method: `GET`
- Path: `/ad/auth/logout`
- 建议权限点：无（或 `auth:logout`）
- Resp: 无 data

### 2.1.3 当前登录用户信息
- Method: `GET`
- Path: `/ad/auth/getLoginInfo`
- 建议权限点：无
- Resp: `data` 为用户信息（`LoginUserResponse`）

---

## 2.2 用户 User（后台）

### 2.2.1 用户分页列表
- Method: `GET`
- Path: `/admin/user/page`
- 建议权限点：`user:list`
- Query:
  - `current`（默认 1）
  - `size`（默认 10）
  - `keyword`（可选，匹配 username/nickname）
  - `status`（可选，`use`/`disable`）
  - `deptId`（可选）
- Resp: `data` 为 IPage<AdUser>（password 字段应为 null）

### 2.2.2 用户详情
- Method: `GET`
- Path: `/admin/user/info/{id}`
- 建议权限点：`user:read`
- Resp: `data` 为 AdUser（当前已聚合 `adRoles`）

### 2.2.3 新增用户
- Method: `POST`
- Path: `/admin/user/create`
- 建议权限点：`user:create`
- Body（示例字段，按后端实体实际支持为准）：
```json
{
  "username": "u001",
  "password": "InitPass123",
  "nickname": "张三",
  "phone": "13800000000",
  "deptId": "xxx",
  "status": "use"
}
```

### 2.2.4 编辑用户
- Method: `PUT`
- Path: `/admin/user/update`
- 建议权限点：`user:update`
- Body: 传 `id` + 需要修改的字段

### 2.2.5 删除用户
- Method: `DELETE`
- Path: `/admin/user/delete/{id}`
- 建议权限点：`user:delete`（清单中未列，可扩展；或复用 `user:update`）

### 2.2.6 批量删除用户
- Method: `DELETE`
- Path: `/admin/user/batch`
- 建议权限点：`user:delete`
- Body:
```json
["id1","id2"]
```

### 2.2.7 启用/禁用用户
- Method: `PUT`
- Path: `/admin/user/changeStatus`
- 建议权限点：`user:update`
- Body:
```json
{ "userId": "xxx", "status": "use" }
```

### 2.2.8 重置密码（管理员）
- Method: `PUT`
- Path: `/admin/user/{id}/reset-password`
- 建议权限点：`user:reset_password`
- Body:
```json
{ "newPassword": "NewPass123" }
```

### 2.2.9 修改密码（个人）
- Method: `PUT`
- Path: `/admin/user/{id}/change-password`
- 建议权限点：无（或 `user:change_password`）
- Body:
```json
{ "oldPassword": "old", "newPassword": "new" }
```

### 2.2.10 分配角色（给用户）
- Method: `PUT`
- Path: `/admin/user/{id}/roles`
- 建议权限点：`role:bind_users`（或复用 `user:update`）
- Body:
```json
["roleId1","roleId2"]
```

### 2.2.11 按部门查询用户
- Method: `GET`
- Path: `/admin/user/dept/{deptId}`
- 建议权限点：`user:list`

### 2.2.12 按角色查询用户
- Method: `GET`
- Path: `/admin/user/role/{roleId}`
- 建议权限点：`user:list`

---

## 2.3 角色 Role（后台）

### 2.3.1 角色分页列表
- Method: `GET`
- Path: `/admin/role/page`
- 建议权限点：`role:list`
- Query: `current`, `size`, `keyword`

### 2.3.2 所有启用角色（下拉）
- Method: `GET`
- Path: `/admin/role/all`
- 建议权限点：`role:list`

### 2.3.3 角色详情
- Method: `GET`
- Path: `/admin/role/{id}`
- 建议权限点：`role:read`

### 2.3.4 创建角色
- Method: `POST`
- Path: `/admin/role`
- 建议权限点：`role:create`
- Body: `name`, `code`, `remark`

### 2.3.5 更新角色
- Method: `PUT`
- Path: `/admin/role`
- 建议权限点：`role:update`

### 2.3.6 删除角色
- Method: `DELETE`
- Path: `/admin/role/{id}`
- 建议权限点：`role:delete`
- 说明：升级方案要求 `ADMIN` 角色不可删除（后端需强约束）。

### 2.3.7 批量删除角色
- Method: `DELETE`
- Path: `/admin/role/batch`
- 建议权限点：`role:delete`

### 2.3.8 角色绑定权限点
- Method: `PUT`
- Path: `/admin/role/{id}/permissions`
- 建议权限点：`role:bind_permissions`
- Body:
```json
["permId1","permId2"]
```

### 2.3.9 根据用户ID查询角色列表（辅助）
- Method: `GET`
- Path: `/admin/role/user/{userId}`
- 建议权限点：`role:list`

---

## 2.4 权限 Permission（后台）

### 2.4.1 权限分页列表
- Method: `GET`
- Path: `/admin/permission/page`
- 建议权限点：`permission:list`
- Query: `current`, `size`, `keyword`

### 2.4.2 权限树（菜单树）
- Method: `GET`
- Path: `/admin/permission/tree`
- 建议权限点：`permission:list`

### 2.4.3 权限详情
- Method: `GET`
- Path: `/admin/permission/{id}`
- 建议权限点：`permission:list`

### 2.4.4 创建权限
- Method: `POST`
- Path: `/admin/permission`
- 建议权限点：`permission:create`（清单未列，可扩展；或复用 `role:bind_permissions`）

### 2.4.5 更新权限
- Method: `PUT`
- Path: `/admin/permission`
- 建议权限点：`permission:update`（同上）

### 2.4.6 删除权限
- Method: `DELETE`
- Path: `/admin/permission/{id}`
- 建议权限点：`permission:delete`（同上）

### 2.4.7 批量删除权限
- Method: `DELETE`
- Path: `/admin/permission/batch`
- 建议权限点：`permission:delete`

### 2.4.8 根据角色查询权限列表（辅助）
- Method: `GET`
- Path: `/admin/permission/role/{roleId}`
- 建议权限点：`permission:list`

### 2.4.9 根据用户查询权限列表（辅助）
- Method: `GET`
- Path: `/admin/permission/user/{userId}`
- 建议权限点：`permission:list`

---

## 2.5 部门 Department（后台）

### 2.5.1 部门分页列表
- Method: `GET`
- Path: `/admin/department/page`
- 建议权限点：`department:list`（可扩展）

### 2.5.2 部门树
- Method: `GET`
- Path: `/admin/department/tree`
- 建议权限点：`department:list`

### 2.5.3 全部启用部门（下拉）
- Method: `GET`
- Path: `/admin/department/all`
- 建议权限点：`department:list`

### 2.5.4 创建/更新/删除/批量删除
- `POST /admin/department`（建议 `department:create`）
- `PUT /admin/department`（建议 `department:update`）
- `DELETE /admin/department/{id}`（建议 `department:delete`）
- `DELETE /admin/department/batch`（建议 `department:delete`）

---

## 2.6 操作日志 Operation Log（后台）

### 2.6.1 操作日志分页
- Method: `GET`
- Path: `/admin/operation-log/page`
- 建议权限点：`audit:list`（或 `operation_log:list`）
- Query:
  - `current`, `size`
  - `userId`、`operationType`、`keyword`
  - `startTime`、`endTime`（格式：`yyyy-MM-dd HH:mm:ss`）

### 2.6.2 操作日志详情
- Method: `GET`
- Path: `/admin/operation-log/{id}`
- 建议权限点：`audit:list`

### 2.6.3 统计/清理
- `GET /admin/operation-log/statistics`
- `DELETE /admin/operation-log/clean?days=30`

---

## 2.7 登录日志 Login Log（升级新增）

> 现状：后端已提供（表 + API），并在登录流程中记录成功/失败。

### 2.7.1 登录日志分页（建议）
- Method: `GET`
- Path: `/admin/login-log/page`
- 建议权限点：`audit:list`
- Query: `current`, `size`, `username`, `success`, `ip`, `loginType`, `startTime`, `endTime`

---

## 2.8 审计日志 Audit Log（升级新增）

> 现状：后端已提供（表 + 写入 AOP + API），对后台关键接口进行审计落库。

### 2.8.1 审计日志分页（建议）
- Method: `GET`
- Path: `/admin/audit-log/page`
- 建议权限点：`audit:list`
- Query: `current`, `size`, `actorUserId`, `action`, `targetType`, `targetId`, `startTime`, `endTime`

---

## 3. 前端按钮权限建议（最小集合）

> 作为前端按钮/菜单显隐的“建议权限点集合”（与清单保持一致）。

- `auth:login`
- `user:list` `user:read` `user:create` `user:update` `user:reset_password`
- `role:list` `role:read` `role:create` `role:update` `role:delete` `role:bind_permissions`
- `permission:list`
- `audit:list`
