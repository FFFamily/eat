package com.tutu.user.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户银行卡信息
 */
@Getter
@Setter
public class AccountBankCard extends BaseEntity {
    
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 账户ID
    private String accountId;
    @TableField(exist = false)
    private String accountName;
    // 开户行
    private String bankName;
    // 银行卡号
    private String cardNumber;
    // 联行号
    private String bankCode;
    // 是否默认
    private String isDefault;
}