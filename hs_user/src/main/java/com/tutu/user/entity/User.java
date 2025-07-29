package com.tutu.user.entity;

import java.math.BigDecimal;
import java.sql.Date;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.user.BaseUserEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseUserEntity {
    // id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 类型
    private String type;
    // 手机号
    private String phone;
    // 身份证
    private String idCard;
    // 用户评分系数
    private BigDecimal score;
    // 纳税人识别号
    private String taxNumber;
    // 开户行
    private String bankName;
    // 账号
    private String bankAccount;
    // 信用代码
    private String creditCode;
    // 地址
    private String address;
    // 联系电话
    private String contactPhone;
    // 走款账户1
    private String bankAccount1;
    // 走款账户2
    private String bankAccount2;
    // 走款账户3
    private String bankAccount3;
    // 注册时间
    private Date registerTime;
}
