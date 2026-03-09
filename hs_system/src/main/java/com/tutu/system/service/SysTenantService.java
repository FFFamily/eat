package com.tutu.system.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.common.constant.CommonConstant;
import com.tutu.common.exceptions.ServiceException;
import com.tutu.system.entity.SysTenant;
import com.tutu.system.mapper.SysTenantMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * 租户服务（全局表）
 */
@Service
public class SysTenantService extends ServiceImpl<SysTenantMapper, SysTenant> {

    /**
     * 按租户编码查询启用租户。
     */
    public SysTenant getActiveByCode(String code) {
        if (StrUtil.isBlank(code)) {
            throw new ServiceException("缺少租户编码");
        }
        LambdaQueryWrapper<SysTenant> w = new LambdaQueryWrapper<>();
        w.eq(SysTenant::getCode, code)
                .eq(SysTenant::getIsDeleted, CommonConstant.NO_STR)
                .eq(SysTenant::getStatus, 1)
                .last("limit 1");
        SysTenant tenant = getOne(w);
        if (tenant == null) {
            throw new ServiceException("租户不存在或已禁用: " + code);
        }
        return tenant;
    }

    public SysTenant getActiveById(String id) {
        if (StrUtil.isBlank(id)) {
            throw new ServiceException("tenantId不能为空");
        }
        LambdaQueryWrapper<SysTenant> w = new LambdaQueryWrapper<>();
        w.eq(SysTenant::getId, id)
                .eq(SysTenant::getIsDeleted, CommonConstant.NO_STR)
                .eq(SysTenant::getStatus, 1)
                .last("limit 1");
        SysTenant tenant = getOne(w);
        if (tenant == null) {
            throw new ServiceException("租户不存在或已禁用: " + id);
        }
        return tenant;
    }

    public List<SysTenant> listActiveByIds(Set<String> ids) {
        if (ids == null || ids.isEmpty()) return List.of();
        LambdaQueryWrapper<SysTenant> w = new LambdaQueryWrapper<>();
        w.in(SysTenant::getId, ids)
                .eq(SysTenant::getIsDeleted, CommonConstant.NO_STR)
                .eq(SysTenant::getStatus, 1);
        return list(w);
    }

    public List<SysTenant> listAllActive() {
        LambdaQueryWrapper<SysTenant> w = new LambdaQueryWrapper<>();
        w.eq(SysTenant::getIsDeleted, CommonConstant.NO_STR)
                .eq(SysTenant::getStatus, 1)
                .orderByAsc(SysTenant::getCode);
        return list(w);
    }
}
