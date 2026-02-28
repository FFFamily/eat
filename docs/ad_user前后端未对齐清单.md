# ad_user 前后端未对齐清单（基于当前代码现状）

范围：
- 后端：`hs_api` 下后台用户体系 Controller（`/admin/**`、`/ad/auth/**`） + `hs_admin_user` 模块（实体/服务）。
- 前端：`web/admin-v3`（后台管理前端）中 admin 用户体系页面与 `src/api/admin/*`。

目标：找出“已实现但未对齐/缺失”的接口、字段、权限点、行为差异，并给出可落地的升级改造项。

---

## P0（会导致功能不可用/权限不生效）

### 1) 部门模块：字段与类型不对齐

现状：
- 前端部门页面 `web/admin-v3/src/views/admin/department/index.vue`：
  - 新增/编辑表单 **没有 code 字段**；仅有 `name/parentId/status/remark`。
  - `status` 使用字符串：`use/disable`。
- 后端部门实体/服务 `hs_admin_user/.../AdDepartment.java` + `AdDepartmentService`：
  - `code` 为业务关键字段，创建时会校验唯一（`findByCode`），并且建表脚本 `sql/2026_02_27_admin_user_table_unify.sql` 中 `ad_department.code` 为 **NOT NULL**。
  - `status` 类型为 `Integer`（1/0）。
  - `parentId` 约定根为 `"0"`；前端清空父级时会传空串 `""`，可能导致树构建丢节点。

影响：
- 前端新增/编辑部门请求会出现：
  - `code` 缺失导致 DB 写入失败或后端逻辑异常。
  - `status` 字符串无法反序列化到 `Integer`，直接 400。
  - `parentId=""` 时树形结构异常。

建议改造：
- 前端：部门表单补齐 `code`；`status` 改为 `1/0`；提交前将空 `parentId` 归一为 `"0"`。
- 后端：创建/更新部门时也做 `parentId` 空白归一（兼容旧前端/脏数据）。

---

### 2) 权限点（perm_code）：前端按钮权限与后端接口权限不一致

现状（代表性差异）：
- 前端按钮权限：
  - 部门：`department:create/update/delete`
  - 权限：`permission:create/update/delete`
  - 用户删除：`user:delete`
  - 用户分配角色按钮：`role:bind_users`
- 后端接口注解（`@PermissionRequired`）：
  - `DepartmentController` 全部复用 `user:update`。
  - `PermissionController` 的增删改复用 `role:bind_permissions`。
  - `AdminUserController` 的删除/批量删除复用 `user:update`。

影响：
- 前端基于 `getPermissionsByUser` 做按钮显隐时：按钮可能被错误隐藏/展示。
- 非 ADMIN/SUPER_ADMIN 用户：可能出现“按钮有，但接口 403”或“按钮没了但其实有权限”的割裂。

建议改造（以“接口权限点”和“按钮权限点”统一为准）：
- 后端：
  - 用户删除/批量删除：改为 `user:delete`。
  - 部门：改为 `department:list/read/create/update/delete`（至少 create/update/delete/list）。
  - 权限：改为 `permission:list/create/update/delete`。
- 前端：
  - 用户“分配角色”按钮的 `v-permission` 从 `role:bind_users` 改为 `user:update`（或后端新增单独权限点并补 seed）。
- SQL：补齐权限点 seed（否则普通角色无法获得新权限点）。

---

### 3) `/admin/permission/tree`：树接口语义与前端使用场景不匹配

现状：
- 后端 `AdPermissionService#getMenuTree()` 仅返回 `type=1`（菜单权限）。Controller `/admin/permission/tree` 直接返回该结果。
- 但当前权限点 seed（`sql/2026_02_27_admin_permission_seed.sql`）插入的最小权限点多为 `type=2`（接口/按钮权限）。
- 前端：
  - 角色授权页面依赖 `/admin/permission/tree` 展示可勾选权限树；如果返回空，则无法授权。
  - 权限管理页“菜单权限树”也依赖该接口。

影响：
- 权限树可能为空，导致“角色授权”功能不可用（或只能看到极少数据）。

建议改造：
- 后端：将 `/admin/permission/tree` 调整为“返回全部权限点树（type=1 + type=2）”。
  - 如果仍需要“仅菜单树”，建议新增 `/admin/permission/menu-tree`。
- 前端：如仅展示菜单树，可在前端过滤 `type===1`（但角色授权建议展示全部）。

---

## P1（功能可用但体验差/存在隐患）

### 4) BaseResponse 错误字段名：前端抛错使用 `res.message`，后端实际为 `msg`

现状：
- 前端拦截器 `web/admin-v3/src/api/request.js`：`Promise.reject(new Error(res.message || "Error"))`。
- 后端 `BaseResponse`：错误信息字段为 `msg`。

影响：
- catch 到的 Error.message 可能丢失后端错误信息（但弹窗 message 目前使用 `res.msg`，因此不一定肉眼可见）。

建议改造：
- 前端统一使用 `res.msg` 作为 Error.message。

---

### 5) 后端实体继承字段重复：`AdUser` 重复声明 `nickname`

现状：
- `AdUser extends BaseUserEntity`，而 `BaseUserEntity` 已包含 `nickname` 字段。
- `AdUser` 又声明了同名 `nickname`，存在字段隐藏（field hiding）。

影响：
- ORM/序列化反射时可能产生不可预期行为（字段重复、赋值覆盖等），属于潜在 bug。

建议改造：
- 删除 `AdUser` 中重复的 `nickname` 字段，保留父类字段。

---

### 6) 部门/权限创建时 parentId 空串兼容性

现状：
- 前端 tree-select 清空后可能提交 `parentId=""`。
- 后端树构建默认根为 `"0"` 或 null。

建议改造：
- 后端在 create/update 时将 `parentId` 的空白值归一为 `"0"`。

---

## P2（可选增强/技术债）

### 7) create 接口返回值语义不统一

现状：
- 多数 create 接口 `BaseResponse.success("创建成功")` 把“提示语”放在 data 中。
- 前端 admin user 创建时尝试把 `res.data` 当作 id（虽是 best-effort，但当前会误判）。

建议：
- 约定：create 成功返回 `data = 新对象id/对象`，提示语放在 `msg`（需要为 BaseResponse 增加带 msg 的 success 工厂方法或直接 new）。

---

## 计划中的升级落地（建议验收标准）

- 部门：新增/编辑/列表/树 全可用；`code/status/parentId` 对齐；不再出现 400/写库失败。
- 权限：角色授权页能展示完整权限树（至少包含 seed 的 type=2 权限点），勾选保存后可生效。
- 权限点：前端按钮显隐与后端接口权限一致；普通角色按权限点授权后可访问对应接口。
- 兼容性：ADMIN/SUPER_ADMIN 仍保持默认放行策略不受影响。
