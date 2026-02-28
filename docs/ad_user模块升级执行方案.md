# ad_user（后台用户体系）缺失功能对照与升级执行方案

> 依据：`/Users/tujunjie/Downloads/project/eat/docs/用户体系功能清单.md`（基础版功能清单）  
> 对照范围：当前后台用户体系相关实现（主要集中在 `hs_admin_user` + `hs_api` 的 admin 控制器/权限配置/登录流程）。

## 1. 当前实现速览（用于对照）

### 1.1 后台用户与 RBAC（已有但未打通）
- 用户/角色/权限/部门：CRUD 基本齐全  
  - `hs_admin_user`：`AdUserService`、`AdRoleService`、`AdPermissionService`、`AdDepartmentService`
  - `hs_api`：`/admin/user`、`/admin/role`、`/admin/permission`、`/admin/department`
- 角色-权限、用户-角色：存在维护接口与关联表实体（但权限校验未接入）
- 权限校验框架：存在 `@PermissionRequired` + AOP（但业务接口基本未标注）
  - `hs_api/src/main/java/com/tutu/api/config/aspect/PermissionAspect.java`
  - `hs_common/src/main/java/com/tutu/common/annotation/PermissionRequired.java`
- Sa-Token 权限数据源：目前为“硬编码”  
  - `hs_api/src/main/java/com/tutu/api/config/permission/PermissionCheckConfig.java`：仅 `ADMIN_ID` 返回固定权限，其他用户返回空

### 1.2 登录与密码（已有但欠缺审计/一致性）
- 登录接口：`/ad/auth/login`（后台）+ `LoginService#doLogin`（通用校验）
- 已做：密码校验/升级（BCrypt + 兼容旧 AES/MD5 升级逻辑）
- 未做：登录日志（成功/失败）、登录失败错误信息隐藏（不暴露“用户是否存在”）

### 1.3 审计/操作日志（有模型与查询、缺写入）
- 有：`AdOperationLog`、`AdOperationLogService`、`OperationLogController`（主要是查询/清理）
- 缺：自动写入机制（缺少 `@OperationLog` 对应的 AOP 记录逻辑；登录日志也缺）

## 2. 与《用户体系功能清单》的差距（缺失/不完整项）

> 说明：以下按清单 1~8 章节列出“当前缺失或不完整”的能力，并指明需要补齐的模块。

### 2.1 数据库与模型（清单第 1 节）
缺失/不完整：
- 缺少“登录日志”模型与落库（清单 `sys_login_log` 等价物）  
  - 当前无专用 login_log 表/实体/Service。
- 缺少“审计日志”规范化模型（清单 `sys_audit_log` 等价物）或缺少写入链路  
  - 当前 `operation_log` 更像“操作日志”，但没有写入 AOP 链路；字段也未对齐清单（request_id、target_type 等）。
- 初始化数据（seed）缺少“权限点与接口映射”的最小集合自动化  
  - 当前权限点更多是菜单树数据（`AdPermission`），但没有“接口权限点（perm_code）”的强约束与校验路径。

### 2.2 认证（清单第 2 节）
缺失/不完整：
- 登录失败不暴露“用户是否存在”（当前会直接返回“用户不存在”）  
  - 需统一为“用户名或密码错误”。
- 记录登录日志（成功/失败、IP、UA、原因）  
  - 当前缺少落库实现。
- token 退出策略：清单允许前端丢弃 token；当前可保持不变（可选增强）。

### 2.3 授权（清单第 3 节）
缺失/不完整：
- “RBAC 权限校验”未真正落地到接口层  
  - `PermissionCheckConfig` 未从 DB 加载用户权限/角色；`PermissionAspect` 也未在 admin 接口普遍启用。
- “ADMIN 默认放行”未按角色实现（目前仅按 `ADMIN_ID` 特判）  
  - 需改为：基于角色码（如 `ADMIN`）或“超级管理员标识”放行。

### 2.4 用户管理（清单第 4 节）
缺失/不完整：
- 用户列表：缺少 status/enabled 过滤参数（当前仅 keyword）  
- 用户详情：缺少“角色信息聚合返回”（`adRoles` 字段存在但未填充）  
- 新增用户：清单要求一次性设置角色；当前需二次调用分配角色接口（可优化）  
- 编辑用户：同理（角色联动更新）  
- 重置密码：清单要求记录审计日志（当前无审计写入链路）

### 2.5 角色与权限管理（清单第 5 节）
缺失/不完整：
- “权限点列表”与“接口权限点”概念未区分（当前更偏菜单权限）  
  - 需要明确：`perm_code`（接口鉴权）与 `menu`（前端导航）是否同表承载；若同表需增加 type/字段规范。
- 角色删除保护：清单要求 `ADMIN` 不允许删除（当前未按 role_code 强约束）

### 2.6 审计日志（清单第 6 节）
缺失：
- 关键操作写入审计日志（用户/角色/绑定变更等）  
- 审计日志列表分页查询（actor/action/target/time range）

### 2.7 权限点最小集合（清单第 7 节）
缺失：
- 后台接口未系统性标注权限点（`@PermissionRequired` 基本未使用）  
- 权限点与接口的清单化/种子化（初始化权限点数据）

### 2.8 SQL 落地规范（清单第 8 节）
现状：
- 已有 `sql/` 目录与版本化脚本习惯，但需补齐“后台用户体系”专用脚本（login_log/audit_log/权限点 seed）。

## 3. 升级执行步骤方案（建议按阶段推进）

> 目标：最短路径让“登录日志 + RBAC 真校验 + 审计日志”闭环跑通，并把 admin 接口权限点体系化。

### Phase 0：基线梳理与统一（1 天）
1. 统一“后台用户表/角色表/权限表”真实表名（驼峰 vs 下划线），补齐实体 `@TableName`（如需要）。  
2. 明确 `AdPermission.code` 是否作为接口 `perm_code`；若不适合，新增字段或新增表 `sys_permission_point`。  
3. 清理接口冲突：`/admin/role` 同路径下同时存在 `RoleController` 与 `AdminRoleController`（需合并/迁移）。  

交付物：
- `docs/ad_user权限点映射表.md`（接口 -> perm_code -> 说明）
 - `sql/2026_02_27_admin_user_table_unify.sql`（后台表名最终统一：snake_case）
 - `sql/2026_02_27_admin_permission_seed.sql`（最小权限点 seed + 角色绑定）
 - `sql/2026_02_27_admin_user_seed.sql`（默认管理员账号 seed + 绑定 SUPER_ADMIN）

### Phase 1：认证与登录日志（1~2 天）
1. 新增登录日志表与实体（建议：`sys_login_log` 或 `ad_login_log`）。字段按清单：username、success、reason、ip、ua、created_at。  
2. 改造 `LoginService#doLogin`：  
   - 登录失败统一错误信息（不暴露用户是否存在）  
   - 成功/失败都记录 login_log（含 IP/UA/原因）  
3. （可选）补齐 `last_login_at` 字段并在成功登录更新（对应清单 sys_user）。  

交付物：
- 新增 SQL：`sql/2026_xx_xx_admin_login_log.sql`  
- 新增代码：LoginLog Entity/Mapper/Service + 在 LoginService 写入

### Phase 2：RBAC 真正打通（2~4 天）
1. 改造 `PermissionCheckConfig`：从 DB 查询当前用户的角色、权限点列表并返回给 Sa-Token  
   - 用户->角色：`admin_user_role` / `AdUserRole`  
   - 角色->权限：`role_permission` / `AdRolePermission`  
   - 权限点：`AdPermission.code`（或新表）  
2. 给 admin 相关接口补齐 `@PermissionRequired` 注解（最小集合按清单第 7 节）  
   - auth:login、user:list/read/create/update/reset_password、role:*、permission:list、audit:list  
3. 超级管理员放行策略（建议双保险）：
   - 固定内置管理员账号：`userId = 1` 默认拥有全部权限（兼容历史环境）
   - 角色码放行：拥有 `SUPER_ADMIN` / `ADMIN` 角色的用户默认放行（不依赖权限点 seed 是否完整）

交付物：
- 新增/更新 SQL：权限点 seed（把清单第 7 节 perm_code 写入权限表）
- Controller 层权限注解覆盖率 >= 90%

### Phase 3：审计日志闭环（2~4 天）
1. 设计审计日志表（建议：`sys_audit_log` 或 `ad_audit_log`），字段按清单：actor_user_id、action、target_type、target_id、detail_json、result、ip、ua、request_id、created_at。
2. 落地审计写入方式（二选一）：
   - A) AOP：对用户/角色/绑定变更等 Service 方法切面记录
   - B) 显式写入：在关键 Service 方法中写入
3. 增加审计日志查询 API（分页 + 条件筛选）。

交付物：
- 新增 SQL：`sql/2026_xx_xx_admin_audit_log.sql`
- 新增接口：`/admin/audit/page`（或复用现有 operation_log 但字段/语义要对齐）

### Phase 4：用户管理体验补齐（1~2 天）
1. 用户列表补齐 enabled/status 过滤。
2. 用户详情聚合返回角色列表（`AdUser.adRoles`）。
3. 新增/编辑用户接口支持“同时提交角色”，减少二次调用。
4. 重置密码/启用禁用/角色分配等动作写入审计日志。

## 4. 验收标准（对应功能清单）
- 登录失败返回统一错误信息；成功/失败均落库 login_log。
- 任意非 ADMIN 用户访问受控接口：无权限返回 403；有权限可通过。
- 后台用户/角色/权限/绑定变更均落库 audit_log，可分页查询与条件筛选。
- `ADMIN` 角色不可删除；权限点可 seed 初始化并与接口对应。

## 5. 建议新增的 SQL 脚本清单
- `sql/2026_xx_xx_admin_login_log.sql`：登录日志表 + 索引
- `sql/2026_xx_xx_admin_audit_log.sql`：审计日志表 + 索引
- `sql/2026_xx_xx_admin_permission_seed.sql`：清单第 7 节权限点初始化（perm_code）
- （可选）`sql/2026_xx_xx_admin_user_last_login.sql`：last_login_at 字段
