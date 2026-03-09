# SaaS 改造（全局用户 + 多租户成员 + 登录后选租户）

> 适用场景：单库单 Schema，行级隔离（`tenant_id`）已落地；现计划把“租户内用户”改为“全局用户”，同一用户可加入多个租户，登录时先验密码再选择租户。
>
> 本文默认前置：已完成多租户基础改造（`TenantLineInnerInterceptor` + `tenant_id` 字段）与“套餐→权限点→租户订阅到期”的授权上限模型（package-only entitlement）。

## 1. 目标与边界

### 1.1 最终目标（最终态）
- 用户表：仅保留全局表 `ad_user`，`username` 全局唯一。
- 成员关系：新增 `ad_user_tenant`，描述“某用户属于哪些租户”，并承载租户内属性（部门/状态等）。
- 部门：租户内定义（`ad_department` 继续 tenant-scoped）。
- RBAC：角色/角色权限仍租户内（`ad_role/ad_user_role/ad_role_permission` 继续 tenant-scoped）。
- 授权上限：保持现有“套餐→权限点→租户订阅到期”逻辑（`sys_package/sys_package_permission/sys_tenant_package`）。
- 登录流程：用户名密码通过后签发**临时 token** → 返回/拉取可选租户列表 → 选择租户（校验订阅有效）→ 签发**正式 token（绑定 tenantId）**。
- 业务请求：不再依赖 `X-Tenant-Code`，Tenant 上下文完全来自正式 token session。

### 1.2 已确认决策（按此落地）
- `ad_user` 与 `account` 合并：最终仅保留 `ad_user`。
- 登录标识：`username` 全局唯一，不允许多个登录名。
- 历史数据：不做合并处理（上线前会删除历史数据，仅保留 `admin`）。
- 部门：一个用户在一个租户内只属于一个部门（单 `dept_id`）。
- 登录交互：方案 B（临时 token → 选租户 → 正式 token）。
- token 策略：一个租户一个 token（切换租户 = 重新签发 token）。
- 租户无有效套餐：**直接拒绝签发正式 token（拒绝登录）**。
- 平台侧能力：提供“以 tenantId 运行（run-as-tenant）”的管理入口（平台接口内部临时 `TenantContext.set(tenantId)` 执行租户 SQL）。

## 2. 数据库改造（DDL）

建议新增脚本：`sql/2026_03_07_global_user_multi_tenant.sql`

### 2.1 `ad_user` 从“租户用户”改为“全局用户”
> 多租户基础改造阶段通常给 `ad_user` 加过 `tenant_id`，本改造需要反转。

- 删除 `ad_user.tenant_id` 列与相关索引
- 恢复/建立：`UNIQUE(username)`

> 由于将清空历史数据，不处理“跨租户同名用户”冲突合并。

### 2.2 新增成员关系表 `ad_user_tenant`（全局表）
用途：登录后列出租户、选租户校验归属、租户内属性承载、RBAC 写入校验。

建议字段：
- `id` varchar(64) PK
- `user_id` varchar(64) NOT NULL
- `tenant_id` varchar(64) NOT NULL
- `dept_id` varchar(64) DEFAULT NULL
- `status` tinyint NOT NULL DEFAULT 1（成员启用/禁用）
- 审计字段：`create_time/update_time/create_by/update_by/is_deleted`

建议索引/约束：
- `UNIQUE(user_id, tenant_id, is_deleted)`
- `KEY idx_aut_user(user_id)`、`KEY idx_aut_tenant(tenant_id)`

### 2.3 RBAC 关联表约束收紧（推荐）
- `ad_user_role`：建议补 `UNIQUE(tenant_id, user_id, role_id, is_deleted)`
- 代码层强制校验：只有存在 `ad_user_tenant(user_id, tenant_id, status=1)` 的用户才能写入该租户的 `ad_user_role`

## 3. 后端改造（Spring + Sa-Token + MyBatis-Plus TenantLine）

### 3.1 TenantContext 获取方式：只认“正式 token session”

目标：移除 `X-Tenant-Code` 依赖；业务请求中 tenantId 只来自正式 token session。

涉及文件：
- `hs_api/src/main/java/com/tutu/api/config/interceptor/TenantContextInterceptor.java`
- `hs_api/src/main/java/com/tutu/api/config/tenant/TenantProperties.java`
- `hs_api/src/main/java/com/tutu/api/config/web/AppWebConfig.java`

改造要点：
- `TenantContextInterceptor`：
  - 不再读取/解析 `X-Tenant-Code`
  - 对登录态：
    - 普通用户：必须从 `StpUtil.getTokenSession()` 取到 `SESSION_TENANT_ID`，否则拒绝（旧 token/非法 token）
    - 平台 admin（`userId=1`）：若 session 有 tenantId 则 set；没有 tenantId 也允许访问平台全局接口
  - 对未登录态：不设置 TenantContext（由拦截器/白名单控制访问范围）
- `TenantProperties.ignorePaths` 增加：
  - `/ad/auth/**`（或精确到 `login/tenants/select-tenant`）
  - 平台接口（如 `/admin/tenant/**`、`/admin/package/**` 等）
- `AppWebConfig`：正式登录校验 `AuthInterceptor` 需对白名单接口放行：
  - `/ad/auth/login`
  - `/ad/auth/tenants`
  - `/ad/auth/select-tenant`

### 3.2 MyBatis-Plus TenantLine ignoreTable 必改
原因：`ad_user` 将不再有 `tenant_id` 列；成员关系表 `ad_user_tenant` 也需要跨租户查询。

涉及文件：
- `hs_api/src/main/java/com/tutu/api/config/mybatis/MybatisPlusConfig.java`

在 `IGNORE_TABLES` 增加：
- `ad_user`
- `ad_user_tenant`

### 3.3 Sa-Token：两套 token（临时 + 正式）

#### 3.3.1 临时 token（pre token）
要求：
- TTL 建议 5 分钟
- 只允许访问“租户列表/选租户”接口
- 使用一次即失效（选租户成功后立即注销/拉黑）

实现建议：
- 新增独立 `loginType=pre` 的 StpLogic（tokenName 例如 `Pre-Token`）
- 新增 `PreAuthInterceptor` 或在 controller 内显式 `PreStpUtil.checkLogin()`

#### 3.3.2 正式 token（现有 Token-Key）
要求：
- 每次签发时必须写入 token session：`SESSION_TENANT_ID`
- 一个租户一个 token（换租户=重新签发）

### 3.4 登录/选租户接口（admin）
涉及文件：
- `hs_api/src/main/java/com/tutu/api/controller/admin/AdUserLoginController.java`

建议接口（3 个）：
1) `POST /ad/auth/login`
- 入参：`username/password`
- 行为：全局查 `ad_user` 校验密码与状态
- 成功：签发临时 token，并返回 `preToken`（可选：顺带返回 tenant 列表）

2) `GET /ad/auth/tenants`
- 需要临时 token
- 返回：该用户可选租户列表（`ad_user_tenant` join `sys_tenant`，过滤 member.status=1 & tenant.status=1）

3) `POST /ad/auth/select-tenant`
- 需要临时 token
- 入参：`tenantId`
- 校验：
  - 用户属于该租户且成员启用（`ad_user_tenant`）
  - 租户启用（`sys_tenant`）
  - 租户当前存在至少一个有效订阅套餐（否则拒绝签发正式 token）
- 成功：签发正式 token，写入 token session：
  - `SESSION_TENANT_ID=tenantId`
  - （可选）`SESSION_TENANT_CODE`
  - （可选）`SESSION_USER_TENANT_ID=ad_user_tenant.id`
- 选租户成功后：注销临时 token（一次性）

### 3.5 权限计算链路：不再从 Header/默认租户兜底
涉及文件：
- `hs_api/src/main/java/com/tutu/api/config/permission/PermissionCheckConfig.java`

要求：
- 普通用户：tenantId 必须来自正式 token session；取不到应提示“请重新登录并选择租户”
- 平台 admin（`userId=1`）：仍返回全量权限（不受套餐限制）
- 其他保持：`effectivePerms = rolePerms ∩ allowedPerms(tenant packages)`

### 3.6 租户内角色分配：加“成员关系存在”校验
涉及模块（示例）：
- `hs_admin_user` 内的用户-角色分配 service

要求：
- 写入 `ad_user_role` 前先校验：`ad_user_tenant(user_id, tenant_id, status=1)` 必须存在
- 继续保留“租户只能分配其套餐允许的权限点”的上限校验（如已有）

## 4. 风控与审计（建议一次做到位）

### 4.1 登录日志的现实问题
现有 `LoginService` 写 `SysLoginLog`（tenant-scoped），在“未选租户”阶段容易因 TenantContext 不存在而写入失败（且异常被吞），导致爆破/失败原因不可追踪。

### 4.2 建议新增全局鉴权日志表 `sys_auth_log`
建议字段：
- `username`、`ip`、`ua`
- `phase`（如 `password_check` / `select_tenant`）
- `success`、`reason`
- `tenant_id`（可空）
- 审计字段

写入规则：
- `/ad/auth/login`（密码校验）写 `tenant_id=NULL`
- `/ad/auth/select-tenant` 写 `tenant_id=选择的 tenantId`

### 4.3 临时 token 安全规则
- TTL 5 分钟
- 仅允许访问 `/ad/auth/tenants`、`/ad/auth/select-tenant`
- 使用一次即失效
- 失败次数限制（按 username + IP，返回统一错误文案）

## 5. 平台“以 tenantId 运行”的管理入口（run-as-tenant）

目标：平台 admin（`userId=1`）不需要拿租户 token，也能执行租户侧 SQL（走 TenantLine）。

建议落地：
- 提供一个工具类 `TenantRunAs.run(tenantId, Supplier<T>)`（保存/恢复旧上下文，`finally` 清理）
- 平台接口（仅 admin）内部显式 `run(tenantId, () -> service.xxx())`

注意：
- 平台接口通常会配置在 `tenant.ignorePaths` 下，不会自动解析 TenantContext，因此必须在接口内部主动设置。

## 6. 前端（admin-v3）改造要点

### 6.1 移除默认携带 `X-Tenant-Code`
当前默认携带位置：
- `web/admin-v3/src/api/request.js`

改造：
- 移除/禁用 `X-Tenant-Code` 注入（避免“未选租户阶段”被误认为 default 租户）

### 6.2 登录页改为“两步”
- Step 1：账号密码登录 → `/ad/auth/login` → 保存 `preToken`
- Step 2：拉租户列表 `/ad/auth/tenants`（带 `Pre-Token`）→ 选择租户 → `/ad/auth/select-tenant` → 保存正式 `Token-Key`

建议体验优化：
- 记住上次租户 `last_tenant_id`
- 租户无有效套餐：前端提示 + 后端仍强制拒绝签发正式 token

## 7. 验收清单（上线前必跑）

1) `username` 全局唯一（不再区分 tenant）
2) 同一用户加入两个租户：`/ad/auth/tenants` 返回两条
3) 选择租户 A 拿 tokenA：只能看到 A 的数据与权限
4) 选择租户 B 拿 tokenB：只能看到 B 的数据与权限
5) 租户无有效套餐：`select-tenant` 直接拒绝，不发正式 token
6) 临时 token 不能访问任何业务接口（仅能 tenants/select-tenant）
7) 平台 run-as-tenant：平台 admin 能指定 tenantId 查看/操作租户数据，且不污染其它请求的 TenantContext

