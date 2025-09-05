package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 经办人
 */
@Data
public class Processor {
    // id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 编号
    private String no;
    // 账号
    private String accountId;
    // 账号名称
    @TableField(exist = false)
    private String accountName;
    // 经办人名称
    private String name;
    // 电话
    private String phone;
    // 身份证
    private String idCard;
    // 住址
    private String address;
}
