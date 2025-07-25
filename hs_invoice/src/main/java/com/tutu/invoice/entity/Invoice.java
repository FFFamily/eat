package com.tutu.invoice.entity;

import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class Invoice extends BaseEntity {
    /**
     * 购买方的名称，个人用户为姓名，企业用户为企业的全称
     */
    private String purchaserName;
    /**
     * 购买方的纳税人识别号，企业购买时必填，个人购买时无需填写
     */
    private String purchaserTaxNumber;
    /**
     * 购买方的联系地址，增值税专用发票中通常需要填写
     */
    private String purchaserAddress;
    /**
     * 购买方的联系电话，增值税专用发票中通常需要填写
     */
    private String purchaserPhone;
    /**
     * 购买方的开户银行名称，增值税专用发票中通常需要填写
     */
    private String purchaserBankName;
    /**
     * 购买方的银行账号，增值税专用发票中通常需要填写
     */
    private String purchaserBankAccount;

    // 销售方信息
    /**
     * 销售方的名称，一般为企业的全称
     */
    private String sellerName;
    /**
     * 销售方的纳税人识别号，由税务机关赋予的唯一标识
     */
    private String sellerTaxNumber;
    /**
     * 销售方的经营地址
     */
    private String sellerAddress;
    /**
     * 销售方的联系电话
     */
    private String sellerPhone;
    /**
     * 销售方的开户银行名称
     */
    private String sellerBankName;
    /**
     * 销售方的银行账号
     */
    private String sellerBankAccount;

    // 发票核心信息
    /**
     * 发票代码，由税务机关统一编码，用于标识发票的种类和归属地等信息
     */
    private String invoiceCode;
    /**
     * 发票号码，与发票代码配合使用，是每张发票的唯一编号
     */
    private String invoiceNumber;
    /**
     * 发票的开具日期，即开票的具体时间
     */
    private Date issueDate;
    /**
     * 销售的货物名称或提供的服务项目名称
     */
    private String goodsOrServiceName;
    /**
     * 销售货物的规格型号，服务类发票可能无需填写
     */
    private String specificationModel;
    /**
     * 销售货物或服务的计量单位，如件、千克、小时等
     */
    private String unit;
    /**
     * 销售货物的数量或服务的时长等数量信息
     */
    private Integer quantity;
    /**
     * 销售货物或服务的单价，不含税价格
     */
    private BigDecimal unitPrice;
    /**
     * 销售货物或服务的不含税金额，即 单价 × 数量
     */
    private BigDecimal amount;
    /**
     * 适用的增值税税率，如 13%、9%、6% 等
     */
    private BigDecimal taxRate;
    /**
     * 根据不含税金额和税率计算得出的增值税税额
     */
    private BigDecimal taxAmount;
    /**
     * 发票的价税合计金额，即不含税金额 + 增值税税额
     */
    private BigDecimal totalAmount;

    // 其他信息
    /**
     * 发票的备注信息，可用于填写合同编号、项目名称等额外说明内容
     */
    private String remarks;
    /**
     * 发票的收款人姓名，即收取款项的人员
     */
    private String payee;
    /**
     * 发票的复核人姓名，负责对发票信息进行审核的人员
     */
    private String reviewer;
    /**
     * 发票的开票人姓名，即实际开具发票的人员
     */
    private String issuer;
}
