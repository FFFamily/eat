# 企业级用户体系 API 草案

> 目标：覆盖认证、组织、权限、用户生命周期、审计等核心能力。

## 1. 认证与会话
- `POST /auth/login`：账号登录（返回 token / session）
- `POST /auth/refresh`：刷新 token
- `POST /auth/logout`：退出登录
- `GET /auth/session`：获取当前会话信息
- `GET /auth/devices`：登录设备列表
- `DELETE /auth/devices/{deviceId}`：踢出设备

## 2. 租户与组织
- `POST /tenants`：创建租户
- `GET /tenants/{id}`：查询租户
- `PUT /tenants/{id}`：更新租户
- `GET /tenants/{id}/org-units`：组织树查询
- `POST /org-units`：新增组织节点
- `PUT /org-units/{id}`：更新组织节点
- `DELETE /org-units/{id}`：删除组织节点（需校验成员）

## 3. 成员与岗位
- `POST /org-members`：新增成员
- `GET /org-members`：成员列表（支持 tenant/org 过滤）
- `PUT /org-members/{id}`：更新成员
- `DELETE /org-members/{id}`：移除成员

## 4. 角色与权限
- `POST /roles`：新增角色
- `GET /roles`：角色列表
- `PUT /roles/{id}`：更新角色
- `DELETE /roles/{id}`：删除角色（需校验关联）
- `POST /permissions`：新增权限
- `GET /permissions`：权限列表
- `PUT /permissions/{id}`：更新权限
- `DELETE /permissions/{id}`：删除权限（需校验关联）
- `PUT /roles/{id}/permissions`：配置角色权限
- `PUT /members/{id}/roles`：配置成员角色

## 5. 用户生命周期
- `POST /users/register`：注册
- `POST /users/invite`：邀请
- `POST /users/approve`：审批
- `PUT /users/{id}/enable`：启用
- `PUT /users/{id}/disable`：禁用
- `POST /users/{id}/reset-password`：重置密码

## 6. 审计与日志
- `GET /audit/login-logs`：登录日志
- `GET /audit/operation-logs`：操作日志
- `GET /audit/permission-change`：权限变更审计
