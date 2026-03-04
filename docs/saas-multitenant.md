# eat SaaS 多租户改造说明（X-Tenant-Code + MyBatis-Plus TenantLine）

## 1. 目标

- 单库单 Schema、共享表结构，通过 `tenant_id` 做行级隔离。
- 前端统一通过 Header `X-Tenant-Code` 传租户编码；后端解析为 `tenant_id` 并写入 Sa-Token token session。
- 所有业务 SQL 默认自动拼 `tenant_id` 条件，避免漏写导致串租户。

## 2. 全局表白名单（不做租户过滤）

后端租户插件忽略以下表（即不会自动拼 `tenant_id` 过滤）：

- `sys_tenant`
- `sys_city`
- `sys_dict_type`
- `sys_dict_data`
- `ad_permission`

> 其余表默认为“租户表”：需要有 `tenant_id` 列，并且所有查询会自动带租户条件。

## 3. 数据库迁移

执行脚本（按需选择性执行）：

- `sql/2026_03_02_saas_multitenant.sql`
- `sql/2026_03_03_add_platform_tenant_menu_permission.sql`（新增租户管理菜单权限码，非破坏性）

迁移核心点：

1) 新增租户表 `sys_tenant`，并插入默认租户：`id=t1`、`code=default`  
2) 给租户表增加 `tenant_id`，并为存量数据填充默认值（或使用默认值 `t1`）  
3) 把唯一约束改为“租户内唯一”（例如 `(tenant_id, username)`）  
4) `account_username_seq` 改为按租户隔离（主键变为 `(tenant_id, account_type_id)`）

## 4. 后端改造点位

### 4.1 租户上下文

- `hs_common/src/main/java/com/tutu/common/tenant/TenantContext.java`
- `hs_common/src/main/java/com/tutu/common/tenant/TenantConstants.java`

### 4.2 请求入口解析租户（先租户、后鉴权）

- `hs_api/src/main/java/com/tutu/api/config/interceptor/TenantContextInterceptor.java`
- 注册顺序：`hs_api/src/main/java/com/tutu/api/config/web/AppWebConfig.java`

行为：
- 优先从 Sa-Token token session 读取 `tenantId`（登录后权威来源）
- 未登录时从 Header `X-Tenant-Code` 解析租户
- 兼容模式（默认）：Header 缺失时回退到默认租户 `t1`
- 严格模式：Header 缺失直接拒绝（除 ignorePaths）

配置：
- `hs_api/src/main/resources/application-dev.yaml`
- `hs_api/src/main/resources/application-prod.yaml`

```yaml
tenant:
  strict: false
  default-tenant-id: t1
  default-tenant-code: default
  ignore-paths:
    - /files/**
    - /actuator/**
    - /druid/**
    - /admin/tenant/**
```

### 4.3 MyBatis-Plus 自动拼租户条件

- `hs_api/src/main/java/com/tutu/api/config/mybatis/MybatisPlusConfig.java`

TenantLineInnerInterceptor：
- tenant 列：`tenant_id`
- tenant 值：`TenantContext.getRequiredTenantId()`
- 忽略表：见“全局表白名单”

### 4.4 INSERT 自动填充 tenant_id

- `hs_api/src/main/java/com/tutu/api/config/mybatis/MyMetaObjectHandler.java`

规则：
- 仅当实体存在 `tenantId` setter 时才填充（避免影响全局表）

### 4.5 实体基类（TenantBaseEntity）

- `hs_common/src/main/java/com/tutu/common/entity/TenantBaseEntity.java`
- `hs_common/src/main/java/com/tutu/common/entity/user/BaseUserEntity.java` 已改为继承 TenantBaseEntity（Account/AdUser 自动具备 tenantId）

## 5. 登录流程约定

登录接口必须携带 Header：`X-Tenant-Code`。

后端登录成功后会把租户信息写入 token session：
- `tenantId`
- `tenantCode`（可选）

点位：
- `hs_api/src/main/java/com/tutu/api/controller/admin/AdUserLoginController.java`
- `hs_api/src/main/java/com/tutu/api/controller/wx/WxLoginController.java`

## 5.1 租户管理（平台能力）

接口：
- `GET /admin/tenant/page`
- `GET /admin/tenant/{id}`
- `POST /admin/tenant`
- `PUT /admin/tenant`
- `PUT /admin/tenant/{id}/status?status=0|1`
- `DELETE /admin/tenant/{id}`
- `POST /admin/tenant/{id}/bootstrap-admin`

实现：
- `hs_api/src/main/java/com/tutu/api/controller/admin/tenant/TenantAdminController.java`

安全说明：
- 为避免普通租户超级管理员越权管理其他租户，当前版本仅允许平台管理员（固定 `userId=1`）访问上述租户管理接口。

## 6. 前端改造要求

所有 API 请求需带：
- `X-Tenant-Code: <tenant_code>`
- `Token-Key: <token>`（登录后）

注意：
- `/files/**` 静态资源请求通常无法携带自定义 Header；本版本默认从租户校验中忽略该路径。

## 7. 验收测试清单（上线前必做）

1) 两个租户可创建相同 `username`（验证唯一键为租户内唯一）
2) default 租户写入业务数据，t2 查询不到（验证隔离）
3) 同一 token 把 `X-Tenant-Code` 改成另一个租户必须被拒绝（验证防重放）
4) 覆盖所有 XML/@Select join SQL 的分页/列表接口（验证 SQL 解析与 tenant 拼接）
