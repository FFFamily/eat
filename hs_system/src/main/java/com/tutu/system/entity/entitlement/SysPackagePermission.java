package com.tutu.system.entity.entitlement;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 套餐-权限点绑定（全局表，不做 tenant 过滤）
 */
@Getter
@Setter
@TableName("sys_package_permission")
public class SysPackagePermission extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String packageId;

    /**
     * ad_permission.id
     */
    private String permissionId;
}

