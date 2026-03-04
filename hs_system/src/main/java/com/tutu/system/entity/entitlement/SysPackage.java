package com.tutu.system.entity.entitlement;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 平台套餐定义（全局表，不做 tenant 过滤）
 */
@Getter
@Setter
@TableName("sys_package")
public class SysPackage extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 套餐编码（唯一）
     */
    private String code;

    /**
     * 套餐名称
     */
    private String name;

    /**
     * 状态：1-启用，0-停用
     */
    private Integer status;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 备注
     */
    private String remark;
}
