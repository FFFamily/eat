package com.tutu.invoice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import com.tutu.invoice.enums.InvoiceHeadType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceHead extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 账号ID，关联用户账号
    private String accountId;
    @TableField(exist = false)
    private String accountName;
    // 发票抬头类型，如个人、企业
    private String type;
    // 发票抬头名称，个人为姓名，企业为企业全称
    private String title;
    // 纳税人识别号，企业必填，个人可不填
    private String taxNumber;
    // 注册地址，企业必填，个人可不填
    private String registeredAddress;
    // 联系电话
    private String phone;
    // 开户行名称
    private String bankName;
    // 联行号
    private String bankCode;
    // 银行账号
    private String bankAccount;
    // 是否为默认抬头（一个账号只能有一个默认）
    private String isDefault;
    
    /**
     * 获取发票抬头类型枚举
     * @return 发票抬头类型枚举
     */
    public InvoiceHeadType getTypeEnum() {
        if (this.type == null) {
            return null;
        }
        try {
            return InvoiceHeadType.valueOf(this.type);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * 设置发票抬头类型枚举
     * @param type 发票抬头类型枚举
     */
    public void setTypeEnum(InvoiceHeadType type) {
        this.type = type != null ? type.name() : null;
    }
}
