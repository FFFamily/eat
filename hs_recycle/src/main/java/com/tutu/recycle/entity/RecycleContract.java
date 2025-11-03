package com.tutu.recycle.entity;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 回收合同
 */
@Getter
@Setter
public class RecycleContract extends BaseEntity{
    // 主键ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 合同名称
    private String name;
    // 合同编号
    private String no;
    // 合同类别
    private String type;
    // 甲方
    private String partyA;
    // 甲方名称
    private String partyAName;
    // 乙方
    private String partyB;
    // 乙方名称
    private String partyBName;
    // 合作方
    private String partner;
    // 合作方名称
    private String partnerName;
    // 合同起始时间 
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;
    // 合同结束时间
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;
    // 合同总金额
    private BigDecimal totalAmount;
    // 主银行卡号
    private String mainBankCard;
    // 主开票信息
    private String mainInvoice;
    // 合同文件路径
    private String filePath;
    
    // 受益人列表（非数据库字段，用于查询展示和传递）
    @TableField(exist = false)
    private List<RecycleContractBeneficiary> beneficiaries;
}
