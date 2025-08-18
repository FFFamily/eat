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
public class Account extends BaseUserEntity {
    // id
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 类型
    private String type;
    // 用户操作使用类型： 普通用户、运输专人
    private String useType;
    // 手机号
    private String phone;
    // 身份证
    private String idCard;
    // 身份证姓名
    private String idCardName;
    // 身份证图片
    private String idCardImg;
    // 用户评分系数
    private BigDecimal score;
    // 纳税人识别号
    private String taxNumber;
    // 开户行
    private String bankName;
    // 账号(银联号)
    private String bankAccount;
    // 信用代码
    private String creditCode;
    // 地址
    private String address;
    // 联系电话
    private String contactPhone;
    // 走款账户1
    private String payAccount1;
    // 走款账户2
    private String payAccount2;
    // 走款账户3
    private String payAccount3;
    // 注册时间
    private Date registerTime;
}
