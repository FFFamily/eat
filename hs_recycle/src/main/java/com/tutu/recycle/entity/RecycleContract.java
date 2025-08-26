package com.tutu.recycle.entity;

import java.math.BigDecimal;
import java.util.Date;

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
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 合同名称
     */
    @TableField("name")
    private String name;

    /**
     * 合同编号
     */
    @TableField("no")
    private String no;

    /**
     * 合同类型：purchase-采购合同, sale-销售合同, transport-运输合同, process-加工合同, storage-仓储合同, other-其他合同
     */
    @TableField("type")
    private String type;
    // 甲方
    @TableField("party_a")
    private String partyA;
    // 乙方
    @TableField("party_b")
    private String partyB;
    /**
     * 合作方名称
     */
    private String partner;
    /**
     * 合同起始时间
     */
    @TableField("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date startTime;

    /**
     * 合同结束时间
     */
    @TableField("end_time")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date endTime;

    /**
     * 合同总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;

    /**
     * 主银行卡号
     */
    @TableField("main_bank_card")
    private String mainBankCard;

    /**
     * 主开票信息
     */
    @TableField("invoice_info")
    private String invoiceInfo;

    /**
     * 合同文件路径
     */
    @TableField("file_path")
    private String filePath;
}
