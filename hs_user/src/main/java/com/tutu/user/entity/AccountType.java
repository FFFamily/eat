package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 账号类型管理实体
 */
@Getter
@Setter
@TableName("account_type")
public class AccountType extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 编号
     */
    private String code;

    /**
     * 账号类型名称
     */
    private String typeName;
} 