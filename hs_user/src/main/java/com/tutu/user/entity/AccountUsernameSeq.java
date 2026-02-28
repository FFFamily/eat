package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 账号用户名序列（按 account_type_id 维度自增）
 * 用于生成类似 CODE+00001 的账号编号，避免并发 COUNT 冲突。
 */
@Getter
@Setter
@TableName("account_username_seq")
public class AccountUsernameSeq {
    @TableId(value = "account_type_id", type = IdType.INPUT)
    private String accountTypeId;

    private Long seq;

    @TableField("update_time")
    private Date updateTime;
}
