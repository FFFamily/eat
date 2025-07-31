package com.tutu.system.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SysContract extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 合同名称
    private String name;
    // 合同识别号
    private String recognitionCode;
    // 合同编码
    private String code;
    // 合同类型
    private String type;
    // 起始日期
    private Date startDate;
    // 结束日期
    private Date endDate;
    // 运输模式
    private String transportMode;
    // 运费承担方式
    private String freightResponsibility;
    // 付款方式
    private String paymentMethod;
    // 开票方式
    private String invoiceMethod;
    // 甲方信息
    private String partyA;
    // 乙方信息
    private String partyB;
}
