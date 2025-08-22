package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 地址管理
 */
@Getter
@Setter
public class Address extends BaseEntity{
    // id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 用户id
    private String accountId;
    // 用户名称
    @TableField(exist = false)
    private String accountName;
    // 分类
    private String category;
    // 地址
    private String realAddress;
    // 备注
    private String remark;

}
