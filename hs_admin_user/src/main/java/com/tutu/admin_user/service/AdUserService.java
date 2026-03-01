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
import com.tutu.admin_user.mapper.AdDepartmentMapper;
import com.tutu.admin_user.mapper.AdRoleMapper;
import com.tutu.admin_user.mapper.AdUserMapper;
import com.tutu.admin_user.mapper.AdUserRoleMapper;
import com.tutu.common.constant.AdminConstant;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.constant.RoleConstant;
import com.tutu.common.enums.user.UserStatusEnum;
import com.tutu.common.exceptions.ServiceException;
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

    /**
     * 根据用户ID列表获取用户Map
     * @param userIds 用户ID列表
     * @return 用户Map
     */
    public HashMap<String,AdUser> getUserMapById(List<String> userIds){
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(AdUser::getId, userIds);
        List<AdUser> users = list(queryWrapper);
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
        return pageListInternal(current, size, keyword, status, deptId, null);
    }

    /**
     * Same as {@link #getPageList(int, int, String, String, String)} but excludes SUPER_ADMIN users.
     */
    public IPage<AdUser> getPageListAssignable(int current, int size, String keyword, String status, String deptId) {
        return pageListInternal(current, size, keyword, status, deptId, getSuperAdminUserIds());
    }

    private IPage<AdUser> pageListInternal(
            int current,
            int size,
            String keyword,
            String status,
            String deptId,
            Set<String> excludeUserIds
    ) {
        Page<AdUser> page = new Page<>(current, size);
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        if (StrUtil.isNotBlank(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                    .like(AdUser::getUsername, keyword)
                    .or()
                    .like(AdUser::getNickname, keyword)
            );
        }
        queryWrapper.eq(StrUtil.isNotBlank(status), AdUser::getStatus, status);
        queryWrapper.eq(StrUtil.isNotBlank(deptId), AdUser::getDeptId, deptId);

        if (excludeUserIds != null && !excludeUserIds.isEmpty()) {
            queryWrapper.notIn(AdUser::getId, excludeUserIds);
        }

        queryWrapper.orderByDesc(AdUser::getCreateTime);
        IPage<AdUser> result = page(page, queryWrapper);
        fillDeptNames(result == null ? null : result.getRecords());
        return result;
    }

    
    @Transactional(rollbackFor = Exception.class)
    public boolean createUser(AdUser user) {
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
        return save(user);
    }

    
    @Transactional(rollbackFor = Exception.class)
    public void updateUser(AdUser user) {
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
                userRole.setCreateTime(new Date());
                userRole.setUpdateTime(new Date());
                userRole.setIsDeleted(CommonConstant.NO_STR);
                adUserRoleMapper.insert(userRole);
            }
        }

        return true;
    }

    
    public List<AdUser> findByDeptId(String deptId) {
        LambdaQueryWrapper<AdUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdUser::getDeptId, deptId)
                .eq(AdUser::getIsDeleted, CommonConstant.NO_STR);
        List<AdUser> users = list(queryWrapper);
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
        fillDeptNames(users);
        return users;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(String id) {
        AdUser user = getById(id);
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        if (AdminConstant.ADMIN_ID.equals(String.valueOf(user.getId()))) {
            throw new ServiceException("不能删除管理员账号");
        }
        // Dangerous op: only allow deleting disabled users.
        if (!UserStatusEnum.DISABLE.getCode().equalsIgnoreCase(String.valueOf(user.getStatus()))) {
            throw new ServiceException("仅允许删除已停用的用户");
        }
        return removeById(id);
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
            if (user == null) {
                throw new ServiceException("用户不存在");
            }
            if (AdminConstant.ADMIN_ID.equals(String.valueOf(user.getId()))) {
                throw new ServiceException("不能删除管理员账号");
            }
            if (!UserStatusEnum.DISABLE.getCode().equalsIgnoreCase(String.valueOf(user.getStatus()))) {
                throw new ServiceException("仅允许删除已停用的用户");
            }
        }
        return removeByIds(ids);
    }
}
