package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 回收订单实体
 */
@Getter
@Setter
public class RecycleOrder extends BaseEntity {
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    // 订单编号
    private String orderNo;
    // 合同编号
    private String contractId;
    // 状态
    private String status;
    // 货物照片
    private String cargoImg;
    // 运输方式(来自合同)
    private String transportType;
    // 经办人签字照片
    private String signImg;
    // === 运输信息
    // 取货时间
    private Date pickupTime;
    // 取货地址
    private String pickupAddress;
    // 取货照片
    private String pickupImg;
    // 处理人(司机)
    private String pickupProcessor;
    // === 分练信息
    // 分练时间
    private Date sortingTime;
    // 分练人
    private String sortingProcessor;
    // 运输成本
    private BigDecimal transportCost;
    // 分练编号
    private String sortingNo;
    // 货物类型
    private String cargoType;
    // 货物名称
    private String cargoName;
    // 货物单价
    private BigDecimal cargoPrice;
    // 重量
    private BigDecimal weight;
    // 分练成本
    private BigDecimal sortingCost;
    // 分练备注
    private String sortingRemark;
    // === 财务
    // 打款时间
    private Date paymentTime;
    // 打款状态
    private String paymentStatus;
    // 打款凭证号
    private String paymentVoucher;
    // 打款公户
    private String paymentAccount;
    // === 发票
    // 开票状态
    private String invoiceStatus;
    // 开票时间
    private Date invoiceTime;
    // 开票金额
    private BigDecimal invoiceAmount;
}
