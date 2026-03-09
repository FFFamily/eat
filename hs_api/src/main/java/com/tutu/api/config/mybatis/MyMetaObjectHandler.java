package com.tutu.api.config.mybatis;


import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.tutu.common.entity.TenantBaseEntity;
import com.tutu.common.tenant.TenantContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
        // Fill tenantId only for tenant-scoped entities (avoid polluting global tables that also have tenant_id column).
        Object origin = metaObject == null ? null : metaObject.getOriginalObject();
        if (origin instanceof TenantBaseEntity) {
            String tenantId = TenantContext.getTenantId();
            if (StrUtil.isNotBlank(tenantId)) {
                this.strictInsertFill(metaObject, "tenantId", String.class, tenantId);
            }
        }
        if (StpUtil.isLogin()) {
            this.strictInsertFill(metaObject, "createBy", String.class, StpUtil.getLoginIdAsString());
            this.strictInsertFill(metaObject, "updateBy", String.class, StpUtil.getLoginIdAsString());
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
        this.strictUpdateFill(metaObject, "updateBy", String.class, StpUtil.getLoginIdAsString());
    }
}
