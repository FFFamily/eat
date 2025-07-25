package com.tutu.invoice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceHead extends BaseEntity {
    @TableId(type = IdType.AUTO)
    private String id;
    // 发票抬头类型，如个人、企业
    private String type;
    // 发票抬头名称，个人为姓名，企业为企业全称
    private String title;
    // 纳税人识别号，企业必填，个人可不填
    private String taxNumber;
    // 企业地址，企业必填，个人可不填
    private String companyAddress;
    // 企业联系电话，企业必填，个人可不填
    private String companyPhone;
    // 企业开户银行，企业必填，个人可不填
    private String bankName;
    // 企业银行账号，企业必填，个人可不填
    private String bankAccount;
    // 联系邮箱，用于接收电子发票
    private String email;
    // 联系电话
    private String phone;
}
