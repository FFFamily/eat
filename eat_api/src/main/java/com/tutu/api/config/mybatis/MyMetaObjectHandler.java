package com.tutu.api.config.mybatis;


import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updateTime", Date.class, new Date());
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
