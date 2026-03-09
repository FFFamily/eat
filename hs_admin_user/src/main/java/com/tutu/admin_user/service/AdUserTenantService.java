package com.tutu.admin_user.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.admin_user.entity.AdUser;
import com.tutu.admin_user.entity.AdUserTenant;
import com.tutu.admin_user.mapper.AdUserTenantMapper;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户-租户成员关系服务（全局表）。
 */
@Service
public class AdUserTenantService extends ServiceImpl<AdUserTenantMapper, AdUserTenant> {

    public List<AdUserTenant> listByUserId(String userId) {
        if (StrUtil.isBlank(userId)) return List.of();
        LambdaQueryWrapper<AdUserTenant> w = new LambdaQueryWrapper<>();
        w.eq(AdUserTenant::getUserId, userId)
                .eq(AdUserTenant::getIsDeleted, CommonConstant.NO_STR);
        return list(w);
    }

    public AdUserTenant getByUserIdAndTenantId(String userId, String tenantId) {
        if (StrUtil.isBlank(userId) || StrUtil.isBlank(tenantId)) return null;
        LambdaQueryWrapper<AdUserTenant> w = new LambdaQueryWrapper<>();
        w.eq(AdUserTenant::getUserId, userId)
                .eq(AdUserTenant::getTenantId, tenantId)
                .eq(AdUserTenant::getIsDeleted, CommonConstant.NO_STR)
                .last("limit 1");
        return getOne(w);
    }

    public void assertEnabledMember(String userId, String tenantId) {
        AdUserTenant link = getByUserIdAndTenantId(userId, tenantId);
        if (link == null) {
            throw new ServiceException("用户不属于该租户");
        }
        if (link.getStatus() != null && link.getStatus() == 0) {
            throw new ServiceException("用户在该租户下已被禁用");
        }
    }

    public IPage<AdUser> pageTenantUsers(int current, int size, String tenantId, String keyword, String status, String deptId) {
        return pageTenantUsers(current, size, tenantId, keyword, status, deptId, null);
    }

    public IPage<AdUser> pageTenantUsers(int current, int size, String tenantId, String keyword, String status, String deptId, List<String> excludeUserIds) {
        if (StrUtil.isBlank(tenantId)) {
            throw new ServiceException("tenantId不能为空");
        }
        Page<AdUser> page = new Page<>(current, size);
        return this.baseMapper.selectTenantUserPage(page, tenantId, keyword, status, deptId, excludeUserIds);
    }
}
