package com.tutu.common.tenant;

import cn.hutool.core.util.StrUtil;
import com.tutu.common.exceptions.ServiceException;

import java.util.function.Supplier;

/**
 * Utility for platform "run as tenant" operations.
 *
 * <p>Use with care: always restore previous context in finally.</p>
 */
public final class TenantRunAs {

    private TenantRunAs() {
    }

    public static <T> T run(String tenantId, Supplier<T> fn) {
        if (StrUtil.isBlank(tenantId)) throw new ServiceException("tenantId不能为空");
        if (fn == null) throw new ServiceException("fn不能为空");

        String oldTid = TenantContext.getTenantId();
        String oldCode = TenantContext.getTenantCode();
        boolean oldIgnore = TenantContext.isIgnoreTenantLine();
        try {
            TenantContext.setTenantId(tenantId);
            TenantContext.setIgnoreTenantLine(false);
            return fn.get();
        } finally {
            TenantContext.clear();
            if (StrUtil.isNotBlank(oldTid)) TenantContext.setTenantId(oldTid);
            if (StrUtil.isNotBlank(oldCode)) TenantContext.setTenantCode(oldCode);
            if (oldIgnore) TenantContext.setIgnoreTenantLine(true);
        }
    }
}
