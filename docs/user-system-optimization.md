# 用户体系代码问题与优化说明（hs_admin_user / hs_user）

> 目标：基于现有源码定位问题并给出可执行的修复方向。优先级说明：P0=高风险/安全/一致性问题；P1=影响业务稳定性；P2=体验与可维护性问题。

## 1. 问题清单（按优先级）

### P0 高风险问题
1. **密码存储与校验方式不一致且不安全**
   - 表现：创建/更新用 AES 可逆加密（`PasswordUtil.encode`），重置/修改密码用 MD5，算法不一致且弱。
   - 风险：无法统一校验、易被破解、密钥硬编码。
   - 位置：
     - `hs_common/src/main/java/com/tutu/common/util/PasswordUtil.java`
     - `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdUserService.java`
     - `hs_user/src/main/java/com/tutu/user/service/AccountService.java`

2. **SQL 注入风险（使用 ${} 拼接）**
   - 表现：自定义 XML 查询用 `${}` 拼接 like 条件。
   - 风险：可被注入；绕过条件过滤。
   - 位置：
     - `hs_user/src/main/resources/mapper/AddressMapper.xml`
     - `hs_user/src/main/resources/mapper/ProcessorMapper.xml`

3. **账号编号生成存在并发冲突**
   - 表现：依赖 `COUNT` 生成 `CODE+00001`，并发下可能重复。
   - 风险：重复账号导致插入失败或数据混乱。
   - 位置：`hs_user/src/main/java/com/tutu/user/service/AccountService.java`

4. **更新逻辑覆盖空值、无完整校验**
   - 表现：`updateUser` 直接加密并覆盖密码；`BeanUtil.copyProperties` 默认会覆盖空值。
   - 风险：误清空字段、密码被置为不可预期值。
   - 位置：
     - `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdUserService.java`
     - `hs_user/src/main/java/com/tutu/user/service/AccountService.java`

5. **账号模型字段语义不清（type vs accountTypeId）**
   - 表现：`Account` 同时存在 `type` 与 `accountTypeId`，生成账号时用 `type` 比较。
   - 风险：字段语义混乱导致业务错误。
   - 位置：`hs_user/src/main/java/com/tutu/user/service/AccountService.java`

### P1 业务稳定性问题
6. **业务账号权限体系不完整**
   - 表现：`Role` 有 `permission` 字段但无权限实体与服务实现。
   - 风险：权限不可配置、难扩展。
   - 位置：`hs_user/src/main/java/com/tutu/user/entity/Role.java`、`hs_user/src/main/java/com/tutu/user/service/RoleService.java`

7. **删除/禁用操作缺少关联校验**
   - 表现：批量删角色、删权限、删部门时不校验被引用关系。
   - 风险：产生孤儿数据或越权。
   - 位置：
     - `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdRoleService.java`
     - `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdPermissionService.java`
     - `hs_admin_user/src/main/java/com/tutu/admin_user/service/AdDepartmentService.java`

8. **软删除过滤缺失**
   - 表现：部分自定义 SQL 未过滤 `is_deleted`。
   - 风险：查询到已删除数据，影响展示与统计。
   - 位置：
     - `hs_user/src/main/resources/mapper/AddressMapper.xml`
     - `hs_user/src/main/resources/mapper/ProcessorMapper.xml`

9. **空数据处理缺失**
   - 表现：`getByCustomerAccountId` 对空列表直接 `getFirst()`。
   - 风险：运行时异常。
   - 位置：`hs_user/src/main/java/com/tutu/user/service/AccountCustomerService.java`

10. **默认银行卡更新未校验记录存在**
    - 表现：`updateDefaultCard` 未校验 `id` 是否存在。
    - 风险：空指针异常或覆盖错误数据。
    - 位置：`hs_user/src/main/java/com/tutu/user/service/AccountBankCardService.java`

### P2 可维护性与体验问题
11. **地址拼接未处理空值**
    - 表现：`realAddress` 直接拼接省市区，可能出现 `null`。
    - 风险：地址展示异常。
    - 位置：`hs_user/src/main/java/com/tutu/user/service/AddressService.java`

12. **部分服务缺少基础校验**
    - 表现：`changeUseType`、`changeAccountBusinessType` 未校验账号是否存在。
    - 风险：潜在 NPE。
    - 位置：`hs_user/src/main/java/com/tutu/user/service/AccountService.java`

## 2. 优化建议（对应修复方向）

### P0 处理优先级
- 统一密码策略：引入 BCrypt/Argon2，移除 AES/MD5，替换 `PasswordUtil`，并补充密码强度与历史策略。
- 修复 SQL 注入：将 `${}` 改为 `#{}` + `CONCAT` 或 `LIKE` 绑定参数。
- 账号编号生成：改用数据库序列/雪花 ID / 事务锁 + 唯一索引兜底。
- 更新逻辑：
  - 管理员更新使用“只更新非空字段”策略（copy ignore null）。
  - 业务账号更新前校验字段、仅在显式更新时修改密码。

### P1 处理优先级
- 补全业务账号权限体系：新增权限表（permission）、角色-权限关联（role_permission）。
- 删除校验：删除角色、权限、部门前增加关联校验或使用软删除+可恢复策略。
- 自定义 SQL 统一加 `is_deleted = 0` 过滤。
- 空值处理：`getByCustomerAccountId` 返回 null 或抛业务异常（包含明确错误信息）。

### P2 处理优先级
- 地址处理：拼接前空值兜底或使用标准化地址结构。
- 账号变更校验：`changeUseType`、`changeAccountBusinessType` 先校验账号存在并记录操作日志。

## 3. 建议的落地顺序
1. 密码安全与 SQL 注入修复（P0）
2. 账号编号与更新逻辑修复（P0）
3. 权限体系补齐与删除校验（P1）
4. 软删除过滤、空值处理与体验优化（P1/P2）
