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
    // 账号类型
    private String type;
    // 用户类型
    private String accountTypeId;
    // 业务类型(供货商、服务商)
    private String businessType;
    // 头像
    private String avatar;
    // 身份
    private String useType;
    // 手机号
    private String phone;
    // 用户评分
    private String score;
    // 用户评分系数
    private String scoreFactor;
    // 纳税人识别号
    private String taxNumber;
    // 积分
    private Long point;
}
