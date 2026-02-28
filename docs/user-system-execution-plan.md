# 用户体系三阶段执行清单（1/2/3 全覆盖）

> 目标：覆盖「P0 修复」「数据库约束/迁移」「企业级表结构与 API 草案」三类任务的可执行清单。后续我将严格按此清单逐项完成。

## 总体原则
- **先安全后扩展**：先处理密码/注入/一致性，再落地数据库约束与企业级能力。
- **向前兼容**：密码升级与字段变更支持存量数据平滑迁移。
- **可验证**：每个步骤都给出验证点与产出物。

---

## Step 1：P0 修复清单（安全与一致性）

### 1.1 密码策略统一（替换 AES/MD5）
- [x] **引入不可逆哈希**：统一使用 BCrypt（替换 `PasswordUtil`）。
- [x] **改造密码校验逻辑**：创建/更新/重置/修改密码入口统一使用 `PasswordUtil`。
- [x] **兼容存量密码**：支持旧 AES/MD5 校验并在首次登录后升级为 BCrypt。
- [x] **字段长度调整**：迁移脚本已提供（`account/ad_user/admin_user` 的 `password` 扩展为 `varchar(255)`）。
- [ ] **覆盖代码位置**：
  - `hs_common/src/main/java/com/tutu/common/util/PasswordUtil.java`
  - `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdUserService.java`
  - `hs_user/src/main/java/com/tutu/user/service/AccountService.java`

**验证点**
- 新增/重置/修改密码后可以登录（需结合现有登录流程验证）。
- 旧密码在首次校验后自动升级到新哈希。

---

### 1.2 SQL 注入修复
- [x] 将 XML 中 `${}` 改为 `#{}` + `CONCAT('%', #{param}, '%')`。
- [x] 统一添加 `is_deleted = 0` 过滤（Address）。

**涉及文件**
- `hs_user/src/main/resources/mapper/AddressMapper.xml`
- `hs_user/src/main/resources/mapper/ProcessorMapper.xml`

**验证点**
- 查询结果一致，且能正确过滤删除数据。
- 输入含特殊字符时无 SQL 注入风险。

---

### 1.3 更新逻辑与空值覆盖修复
- [x] 管理员更新使用“仅更新非空字段”策略（copy ignore null）。
- [x] 业务账号更新仅在显式传入新密码时更新密码。
- [x] `changeUseType` / `changeAccountBusinessType` 先校验用户存在。

**涉及文件**
- `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdUserService.java`
- `hs_user/src/main/java/com/tutu/user/service/AccountService.java`

---

### 1.4 账号编号并发冲突修复
- [x] 方案选择：
  - 方案 A：新增 `account_type_seq` 表（或 `account_type.next_no`），使用行级锁生成序号。
  - 方案 B：直接使用雪花 ID / Redis 序列，并保留前缀。
- [x] 已落地：使用 `account_username_seq`（MySQL 序列表模拟序列，原子自增）生成 `CODE+00001`。
- [x] 加唯一约束兜底：迁移脚本已提供 `account.username` 唯一索引。

**验证点**
- 并发创建 100+ 账号，无重复账号名。

---

## Step 2：数据库约束 / 迁移清单

### 2.1 唯一约束与索引
- [ ] `account.username`、`account.phone` 唯一索引
- [ ] `account_type.code` 唯一索引
- [ ] `role.code`、`ad_role.code`、`ad_permission.code` 唯一索引
- [ ] `ad_department.code` 唯一索引

### 2.2 外键与数据完整性
- [ ] `account_customer.account_id` → `account.id`
- [ ] `account_customer.customer_account_id` → `account.id`
- [ ] `account_bank_card.account_id` → `account.id`
- [ ] `address.account_id` → `account.id`
- [ ] `account_service_scope.account_id` → `account.id`
- [ ] `ad_user_role.user_id` → `ad_user.id`
- [ ] `ad_user_role.role_id` → `ad_role.id`
- [ ] `ad_role_permission.role_id` → `ad_role.id`
- [ ] `ad_role_permission.permission_id` → `ad_permission.id`

### 2.3 字段变更
- [ ] `password` 字段长度扩展（BCrypt）
- [ ] 为软删除表统一默认值与索引（`is_deleted`）

### 2.4 迁移脚本输出
- [x] 在 `/Users/tujunjie/Downloads/project/eat/sql/` 新增迁移 SQL（`2026_02_25_user_auth_migration.sql`）。
- [x] 输出数据修复脚本（`2026_02_25_user_auth_data_fix.sql`，已修复语法问题）。

**验证点**
- 迁移前后数据量一致；违反约束的数据可定位。
- 新约束生效。

---

## Step 3：企业级用户体系表结构 + API 草案

### 3.1 新增表结构设计（DDL）
- [ ] `tenant`（租户）
- [ ] `org_unit`（组织/部门树）
- [ ] `org_member`（成员）
- [ ] `member_role`（成员-角色）
- [ ] `role` / `permission` / `role_permission`（统一权限模型）
- [ ] `login_session` / `login_device` / `login_log`
- [ ] `auth_factor`（MFA）
- [ ] `user_profile`（业务档案）

### 3.2 ERD & 字段规范
- [ ] 输出 ERD（markdown + mermaid）。
- [ ] 明确字段规范（命名、索引、软删除、审计字段）。

### 3.3 API 草案输出
- [ ] 登录与认证 API（登录/刷新/登出/设备管理）
- [ ] 租户/组织管理 API
- [ ] 角色/权限/数据范围 API
- [ ] 用户生命周期 API（注册/邀请/审核/冻结/注销）
- [ ] 审计与日志 API

**产出物**
- [x] `/Users/tujunjie/Downloads/project/eat/docs/enterprise-user-schema.md`
- [x] `/Users/tujunjie/Downloads/project/eat/docs/enterprise-user-api.md`

---

## 执行顺序（我将按此顺序落地）
1. Step 1 全部完成并验证
2. Step 2 迁移脚本与约束落地
3. Step 3 设计文档与 API 草案输出

---

## 你需要确认的事项（仅确认一次）
### 1) 密码升级策略（旧密码迁移方式）
- **选项 A：首次登录升级（推荐）**
  - 逻辑：用户登录校验旧算法成功后，立即用新算法重算并替换存储。
  - 优点：对用户无感；迁移成本低；可分批渐进升级。
  - 风险：长时间不登录的账号仍保留旧密码；需要保留旧算法校验逻辑一段时间。
  - 适用：用户量大、不能打扰用户、需要平滑迁移。
- **选项 B：一次性强制全量重置**
  - 逻辑：统一要求用户在首次访问时重置密码，或后台批量生成临时密码。
  - 优点：安全切换快；旧算法可快速下线。
  - 风险：业务干扰大；客服/通知成本高；可能造成登录失败投诉。
  - 适用：用户量可控、合规要求严格、可以统一通知。

### 2) 账号编号生成方案（并发唯一性）
- **选项 A：数据库计数表 + 行级锁（推荐）**
  - 逻辑：为每个账号类型维护计数表（如 `account_type_seq`），创建账号时 `FOR UPDATE` 自增。
  - 优点：实现简单、事务内可控、无需外部依赖。
  - 风险：高并发下行锁竞争，需要合理索引。
  - 适用：并发中等、单库部署。
- **选项 B：数据库“序列表”（MySQL 推荐做法）**
  - 逻辑：MySQL 无原生 sequence，使用一张 `account_username_seq` 表模拟序列，配合 `INSERT ... ON DUPLICATE KEY UPDATE seq=LAST_INSERT_ID(seq+1)` 原子自增。
  - 优点：实现简洁、并发安全、无需外部依赖；对业务透明。
  - 风险：需要确保调用在事务/同连接内读取 `LAST_INSERT_ID()`（已通过事务控制）。
  - 适用：MySQL 单库部署、需要按类型自增编号。
- **选项 C：Redis 序列**
  - 逻辑：Redis `INCR` 生成序号，业务侧拼接前缀。
  - 优点：高并发性能好、可跨服务。
  - 风险：引入 Redis 依赖；需处理一致性与容灾。
  - 适用：高并发场景、已有 Redis 基础设施。

### 3) 外键策略（数据完整性）
- **选项 A：仅校验阻止删除（推荐）**
  - 逻辑：删除前先检查关联记录，若有关联则禁止删除。
  - 优点：数据安全；可控性强；适合“逻辑删除”模型。
  - 风险：需要额外校验代码；数据清理流程更复杂。
  - 适用：企业级系统常见做法。
- **选项 B：允许级联删除**
  - 逻辑：外键设置 `ON DELETE CASCADE`，父记录删除自动清理子记录。
  - 优点：实现简单；避免脏数据。
  - 风险：误删风险高；审计和恢复困难。
  - 适用：低风险数据或临时/测试系统。

> 说明：如果你暂未确认，我会在执行时默认采用“首次登录升级 + 计数表行锁 + 外键阻止删除”的稳妥方案。

### 4) 权限点模型（菜单权限 vs 接口权限）
- **选项 A：复用 `AdPermission.code` 作为接口 `perm_code`（当前实现）**
  - 逻辑：接口鉴权与菜单/按钮权限复用一张权限表，通过 `type`（菜单/按钮）区分。
  - 优点：表少、实现快；权限点可直接配置到角色。
  - 风险：菜单 code 与接口 code 需要统一规范；历史数据可能不满足（需补齐 seed/约束）。
- **选项 B：新增接口权限点表（推荐长期）**
  - 逻辑：新增 `sys_permission_point`（只承载 `perm_code/perm_name`），菜单表只管导航；两者可通过绑定或独立使用。
  - 优点：概念清晰；避免菜单 code 与接口 code 混用；更适合企业级扩展。
  - 风险：需要额外表与绑定逻辑；改造成本更高。

### 5) 超级管理员放行策略
- **选项 A：按角色码放行（当前实现）**
  - 逻辑：拥有 `SUPER_ADMIN/ADMIN` 角色的用户跳过权限点校验。
  - 优点：不依赖“权限点 seed 是否完整”；上线更稳。
  - 风险：需要确保超级管理员角色不可被误赋权。
- **选项 B：按固定用户ID放行（不推荐）**
  - 优点：实现最简单。
  - 风险：环境迁移/导入后易失效；扩展性差。

> 当前项目已采用“双保险”：
> - 固定 `userId = 1`（`AdminConstant.ADMIN_ID`）直接拥有全部权限；
> - 同时支持 `SUPER_ADMIN/ADMIN` 角色码放行（推荐作为主要策略）。

### 你已确认的选择
- 账号编号：使用 **数据库序列表（account_username_seq）**。
- 外键策略：**不使用级联删除**，仅做代码层删除校验。
