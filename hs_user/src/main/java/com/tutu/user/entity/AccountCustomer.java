package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 账户客户关系表
 */
@Getter
@Setter
@TableName("account_customer")
public class AccountCustomer extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 账户ID
     */
    private String accountId;
    
    /**
     * 客户账户ID
     */
    private String customerAccountId;
    
    /**
     * 账户名称（关联查询字段，不映射到数据库）
     */
    @TableField(exist = false)
    private String accountName;
    
    /**
     * 客户账户名称（关联查询字段，不映射到数据库）
     */
    @TableField(exist = false)
    private String customerAccountName;
}

