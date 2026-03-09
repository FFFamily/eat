package com.tutu.admin_user.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.AdUserRole;
import com.tutu.admin_user.entity.AdDepartment;
import com.tutu.admin_user.entity.AdRole;
import com.tutu.admin_user.entity.AdUserTenant;
import com.tutu.admin_user.mapper.AdDepartmentMapper;
import com.tutu.admin_user.mapper.AdRoleMapper;
import com.tutu.admin_user.mapper.AdUserMapper;
import com.tutu.admin_user.mapper.AdUserRoleMapper;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.constant.RoleConstant;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.common.tenant.TenantContext;
import com.tutu.common.util.PasswordUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 管理员用户服务实现类
 */
@Service("adUserService")
public class AdUserService extends ServiceImpl<AdUserMapper, AdUser> {

    @Resource
    private AdUserRoleMapper adUserRoleMapper;

    @Resource
    private AdDepartmentMapper adDepartmentMapper;

    @Resource
    private AdRoleMapper adRoleMapper;

    @Resource
    private AdUserTenantService adUserTenantService;

    /**
     * Attach department names to user records (same API response, no extra HTTP calls).
     */
    private void fillDeptNames(List<AdUser> users) {
        if (users == null || users.isEmpty()) {
            return;
        }
        Set<String> deptIds = users.stream()
                .map(AdUser::getDeptId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());
        if (deptIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<AdDepartment> w = new LambdaQueryWrapper<>();
        w.in(AdDepartment::getId, deptIds)
                .eq(AdDepartment::getIsDeleted, CommonConstant.NO_STR);
        List<AdDepartment> depts = adDepartmentMapper.selectList(w);
        if (depts == null) {
            depts = List.of();
        }
        Map<String, String> idToName = depts.stream()
                .filter(d -> d != null && d.getId() != null)
                .collect(Collectors.toMap(AdDepartment::getId, AdDepartment::getName, (a, b) -> a));

        users.forEach(u -> {
            if (u == null) return;
            String did = u.getDeptId();
            if (did == null || did.isBlank()) return;
            if (u.getDeptName() == null || u.getDeptName().isBlank()) {
                u.setDeptName(idToName.get(did));
            }
        });
    }

    private void fillTenantMemberFields(List<AdUser> users) {
        if (users == null || users.isEmpty()) return;
        String tenantId = TenantContext.getTenantId();
        if (StrUtil.isBlank(tenantId)) return;

        Set<String> userIds = users.stream()
                .map(AdUser::getId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) return;

        LambdaQueryWrapper<AdUserTenant> w = new LambdaQueryWrapper<>();
        w.eq(AdUserTenant::getTenantId, tenantId)
                .in(AdUserTenant::getUserId, userIds)
                .eq(AdUserTenant::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserTenant> links = adUserTenantService.list(w);
        if (links == null || links.isEmpty()) return;

        Map<String, String> userToDept = links.stream()
                .filter(l -> l != null && StrUtil.isNotBlank(l.getUserId()))
                .collect(Collectors.toMap(AdUserTenant::getUserId, AdUserTenant::getDeptId, (a, b) -> a));

        users.forEach(u -> {
            if (u == null) return;
            if (StrUtil.isBlank(u.getDeptId())) {
                u.setDeptId(userToDept.get(u.getId()));
            }
        });
    }

    /**
     * Built-in SUPER_ADMIN users should not be selectable in role assignment UI.
     */
    private Set<String> getSuperAdminUserIds() {
        LambdaQueryWrapper<AdRole> roleW = new LambdaQueryWrapper<>();
        roleW.eq(AdRole::getCode, RoleConstant.SUPER_ADMIN)
                .eq(AdRole::getIsDeleted, CommonConstant.NO_STR)
                .last("limit 1");
        AdRole superRole = adRoleMapper.selectOne(roleW);
        if (superRole == null || StrUtil.isBlank(superRole.getId())) {
            return Set.of();
        }

        LambdaQueryWrapper<AdUserRole> urW = new LambdaQueryWrapper<>();
        urW.eq(AdUserRole::getRoleId, superRole.getId())
                .eq(AdUserRole::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserRole> links = adUserRoleMapper.selectList(urW);
        if (links == null || links.isEmpty()) {
            return Set.of();
        }
        return links.stream()
                .map(AdUserRole::getUserId)
                .filter(id -> id != null && !id.isBlank())
                .collect(Collectors.toSet());
    }

    
    public AdUser findByUsername(String username) {
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getUsername, username);
        return getOne(queryWrapper);
    }

    public AdUser getTenantUserById(String userId) {
        if (StrUtil.isBlank(userId)) return null;
        AdUser u = getById(userId);
        if (u == null) return null;
        List<AdUser> list = List.of(u);
        fillTenantMemberFields(list);
        fillDeptNames(list);
        return u;
    }

    /**
     * 根据用户ID列表获取用户Map
     * @param userIds 用户ID列表
     * @return 用户Map
     */
    public HashMap<String,AdUser> getUserMapById(List<String> userIds){
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AdUser::getId, userIds);
        List<AdUser> users = list(queryWrapper);
        fillTenantMemberFields(users);
        fillDeptNames(users);
        HashMap<String,AdUser> userMap = new HashMap<>();
        users.forEach(user -> userMap.put(user.getId(), user));
        return userMap;
    }

    /**
     * 获取用户分页列表
     * @param current 当前页
     * @param size 每页数量
     * @param keyword 搜索关键词
     * @param status 状态（可选，use/disable）
     * @param deptId 部门（可选）
     * @return 用户分页列表
     */
    public IPage<AdUser> getPageList(int current, int size, String keyword, String status, String deptId) {
        // Platform admin "all tenants" read mode: show global users across all tenants.
        // Note: deptId is a tenant-member attribute and is not meaningful across tenants, so it's ignored here.
        if (TenantContext.isIgnoreTenantLine()) {
            return pageAllUsers(current, size, keyword, status, null);
        }

        String tenantId = TenantContext.getRequiredTenantId();
        IPage<AdUser> page = adUserTenantService.pageTenantUsers(current, size, tenantId, keyword, status, deptId, null);
        fillDeptNames(page == null ? null : page.getRecords());
        return page;
    }

    /**
     * Same as {@link #getPageList(int, int, String, String, String)} but excludes SUPER_ADMIN users.
     */
    public IPage<AdUser> getPageListAssignable(int current, int size, String keyword, String status, String deptId) {
        List<String> exclude = getSuperAdminUserIds().stream().toList();

        // Platform admin "all tenants" read mode: show global users across all tenants.
        if (TenantContext.isIgnoreTenantLine()) {
            return pageAllUsers(current, size, keyword, status, exclude);
        }

        String tenantId = TenantContext.getRequiredTenantId();
        IPage<AdUser> page = adUserTenantService.pageTenantUsers(current, size, tenantId, keyword, status, deptId, exclude);
        fillDeptNames(page == null ? null : page.getRecords());
        return page;
    }

    private IPage<AdUser> pageAllUsers(int current, int size, String keyword, String status, List<String> excludeUserIds) {
        Page<AdUser> pageReq = new Page<>(current, size);
        LambdaQueryWrapper<AdUser> w = new LambdaQueryWrapper<>();
        w.eq(AdUser::getIsDeleted, CommonConstant.NO_STR);

        if (StrUtil.isNotBlank(status)) {
            w.eq(AdUser::getStatus, status);
        }
        if (StrUtil.isNotBlank(keyword)) {
            String kw = keyword.trim();
            w.and(x -> x.like(AdUser::getUsername, kw).or().like(AdUser::getNickname, kw));
        }
        if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
            w.notIn(AdUser::getId, excludeUserIds);
        }
        w.orderByDesc(AdUser::getCreateTime);
        return page(pageReq, w);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(AdUser user) {
        String tenantId = TenantContext.getRequiredTenantId();
        // 检查用户名是否已存在
        if (findByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        // 设置默认密码并加密
        if (StrUtil.isBlank(user.getPassword())) {
            throw new ServiceException("缺失密码");
        } else {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        }
        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(UserStatusEnum.USE.getCode());
        }
        boolean ok = save(user);
        if (!ok) return false;

        // Create member link for current tenant
        AdUserTenant link = new AdUserTenant();
        link.setUserId(user.getId());
        link.setTenantId(tenantId);
        link.setDeptId(user.getDeptId());
        link.setStatus(CommonConstant.YES_INT);
        link.setIsDeleted(CommonConstant.NO_STR);
        adUserTenantService.save(link);
        return true;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(AdUser user) {
        String tenantId = TenantContext.getRequiredTenantId();
        AdUser existUser = getById(user.getId());
        if (existUser == null) {
            throw new ServiceException("用户不存在");
        }
        String id = existUser.getId();
        if (id.equals(AdminConstant.ADMIN_ID)) {
            throw new ServiceException("不能修改管理员账号");
        }
        // 检查用户名是否被其他用户使用
        if (StrUtil.isNotBlank(user.getUsername())) {
            LambdaQueryWrapper<AdUser> usernameWrapper = new LambdaQueryWrapper<>();
            usernameWrapper.eq(AdUser::getUsername, user.getUsername())
                    .ne(AdUser::getId, user.getId());
            if (getOne(usernameWrapper) != null) {
                throw new ServiceException("用户名已被使用");
            }
        }
        // 检查手机号是否被其他用户使用
        if (StrUtil.isNotBlank(user.getPhone())) {
            LambdaQueryWrapper<AdUser> phoneWrapper = new LambdaQueryWrapper<>();
            phoneWrapper.eq(AdUser::getPhone, user.getPhone())
                    .ne(AdUser::getId, user.getId());
            if (getOne(phoneWrapper) != null) {
                throw new ServiceException("手机号已被使用");
            }
        }
        // 加密密码
        if (StrUtil.isNotBlank(user.getPassword())) {
            user.setPassword(PasswordUtil.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        BeanUtil.copyProperties(user, existUser, CopyOptions.create().setIgnoreNullValue(true));
        updateById(existUser);

        // Update tenant member fields (deptId)
        if (user.getDeptId() != null) {
            AdUserTenant link = adUserTenantService.getByUserIdAndTenantId(user.getId(), tenantId);
            if (link == null) {
                // backward compat: if missing link, create it
                link = new AdUserTenant();
                link.setUserId(user.getId());
                link.setTenantId(tenantId);
                link.setDeptId(user.getDeptId());
                link.setStatus(CommonConstant.YES_INT);
                link.setIsDeleted(CommonConstant.NO_STR);
                adUserTenantService.save(link);
            } else {
                link.setDeptId(user.getDeptId());
                adUserTenantService.updateById(link);
            }
        }
    }

    
    public boolean resetPassword(String id, String newPassword) {
        AdUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, id)
                .set(AdUser::getPassword, PasswordUtil.encode(newPassword))
                .set(AdUser::getUpdateTime, new Date());

        return update(updateWrapper);
    }

    
    public boolean changePassword(String id, String oldPassword, String newPassword) {
        AdUser user = getById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!PasswordUtil.match(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }

        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, id)
                .set(AdUser::getPassword, PasswordUtil.encode(newPassword))
                .set(AdUser::getUpdateTime, new Date());

        return update(updateWrapper);
    }

    public void updatePasswordById(String id, String encodedPassword) {
        if (StrUtil.isBlank(id) || StrUtil.isBlank(encodedPassword)) {
            return;
        }
        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, id)
                .set(AdUser::getPassword, encodedPassword)
                .set(AdUser::getUpdateTime, new Date());
        update(updateWrapper);
    }


    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 状态
     */
    public void changeStatus(String userId, String status) {
        LambdaUpdateWrapper<AdUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AdUser::getId, userId)
                .set(AdUser::getStatus, status)
                .set(AdUser::getUpdateTime, new Date());
        update(updateWrapper);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean assignRoles(String userId, List<String> roleIds) {
        String tenantId = TenantContext.getRequiredTenantId();
        adUserTenantService.assertEnabledMember(userId, tenantId);

        // 先删除原有角色关联
        LambdaUpdateWrapper<AdUserRole> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(AdUserRole::getUserId, userId)
                .set(AdUserRole::getIsDeleted, CommonConstant.YES_STR)
                .set(AdUserRole::getUpdateTime, new Date());
        adUserRoleMapper.update(null, deleteWrapper);

        // 添加新的角色关联
        if (roleIds != null && !roleIds.isEmpty()) {
            for (String roleId : roleIds) {
                AdUserRole userRole = new AdUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                // tenant_id will be filled by TenantLine/MetaObjectHandler
                userRole.setCreateTime(new Date());
                userRole.setUpdateTime(new Date());
                userRole.setIsDeleted(CommonConstant.NO_STR);
                adUserRoleMapper.insert(userRole);
            }
        }

        return true;
    }

    
    public List<AdUser> findByDeptId(String deptId) {
        String tenantId = TenantContext.getRequiredTenantId();
        LambdaQueryWrapper<AdUserTenant> w = new LambdaQueryWrapper<>();
        w.eq(AdUserTenant::getTenantId, tenantId)
                .eq(AdUserTenant::getDeptId, deptId)
                .eq(AdUserTenant::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserTenant> links = adUserTenantService.list(w);
        if (links == null || links.isEmpty()) return List.of();
        List<String> userIds = links.stream().map(AdUserTenant::getUserId).filter(StrUtil::isNotBlank).toList();
        if (userIds.isEmpty()) return List.of();

        LambdaQueryWrapper<AdUser> userW = new LambdaQueryWrapper<>();
        userW.in(AdUser::getId, userIds)
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);
        List<AdUser> users = list(userW);
        fillTenantMemberFields(users);
        fillDeptNames(users);
        return users;
    }

    
    public List<AdUser> findByRoleId(String roleId) {
        // 先查询用户角色关联表
        LambdaQueryWrapper<AdUserRole> roleQueryWrapper = new LambdaQueryWrapper<>();
        roleQueryWrapper.eq(AdUserRole::getRoleId, roleId)
                .eq(AdUserRole::getIsDeleted, CommonConstant.NO_STR);
        List<AdUserRole> userRoles = adUserRoleMapper.selectList(roleQueryWrapper);

        if (userRoles.isEmpty()) {
            return List.of();
        }

        // 提取用户ID列表
        List<String> userIds = userRoles.stream()
                .map(AdUserRole::getUserId)
                .toList();

        // 查询用户信息
        LambdaQueryWrapper<AdUser> userQueryWrapper = new LambdaQueryWrapper<>();
        userQueryWrapper.in(AdUser::getId, userIds)
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);

        List<AdUser> users = list(userQueryWrapper);
        fillTenantMemberFields(users);
        fillDeptNames(users);
        return users;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String id) {
        String tenantId = TenantContext.getRequiredTenantId();
        AdUser user = getById(id);
        if (user == null) throw new ServiceException("用户不存在");
        if (AdminConstant.ADMIN_ID.equals(String.valueOf(user.getId()))) {
            throw new ServiceException("不能删除管理员账号");
        }

        // 1) remove member link in current tenant
        AdUserTenant link = adUserTenantService.getByUserIdAndTenantId(id, tenantId);
        if (link != null) {
            link.setIsDeleted(CommonConstant.YES_STR);
            adUserTenantService.updateById(link);
        }

        // 2) remove roles in current tenant (tenant filter applied by TenantLine)
        LambdaUpdateWrapper<AdUserRole> deleteWrapper = new LambdaUpdateWrapper<>();
        deleteWrapper.eq(AdUserRole::getUserId, id)
                .set(AdUserRole::getIsDeleted, CommonConstant.YES_STR)
                .set(AdUserRole::getUpdateTime, new Date());
        adUserRoleMapper.update(null, deleteWrapper);

        // 3) if no membership left, delete global user (soft delete)
        if (adUserTenantService.listByUserId(id).isEmpty()) {
            return removeById(id);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteUsers(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return true;
        }
        // Validate all before deleting to avoid partial deletes.
        List<AdUser> users = listByIds(ids);
        if (users == null || users.size() != ids.size()) {
            throw new ServiceException("用户不存在");
        }
        for (AdUser user : users) {
            if (user == null) throw new ServiceException("用户不存在");
            if (AdminConstant.ADMIN_ID.equals(String.valueOf(user.getId()))) {
                throw new ServiceException("不能删除管理员账号");
            }
        }
        boolean ok = true;
        for (String id : ids) {
            ok = ok && deleteUser(id);
        }
        return ok;
    }
}
