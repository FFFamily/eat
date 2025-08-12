package com.tutu.system.entity;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startDate;
    // 结束日期
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endDate;
    // // 付款方式
    // private String paymentMethod;
    // // 开票方式
    // private String invoiceMethod;
    // 签署用户ID
    private String userId;
}
