package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
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
       /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 订单编号
     */
    @TableField("no")
    private String no;

    /**
     * 订单类型
     */
    @TableField("type")
    private String type;

    /**
     * 订单状态
     */
    @TableField("status")
    private String status;

    /**
     * 起始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("start_time")
    private Date startTime;

    /**
     * 结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("end_time")
    private Date endTime;

    /**
     * 订单识别码
     */
    @TableField("identify_code")
    private String identifyCode;

    /**
     * 经办人
     */
    @TableField("processor")
    private String processor;

    /**
     * 经办人电话
     */
    @TableField("processor_phone")
    private String processorPhone;

    /**
     * 订单总金额
     */
    @TableField("total_amount")
    private BigDecimal totalAmount;


    /**
     * 订单图片
     */
    @TableField("order_node_img")
    private String orderNodeImg;
    // 合同id
    private String contractId;
    // 合同编号
    private String contractNo;
    // 合同名称
    private String contractName;
    // 合同合作方
    private String contractPartner;
    // 合同合作方名称
    private String contractPartnerName;
    // 甲方
    private String partyA;
    // 甲方名称
    private String partyAName;
    // 乙方
    private String partyB;
    // 乙方名称
    private String partyBName;
    // 合同金额
    private BigDecimal contractPrice;
    /**
     * 仓库地址
     */
    @TableField("warehouse_address")
    private String warehouseAddress;

    /**
     * 交付地址
     */
    @TableField("delivery_address")
    private String deliveryAddress;

    /**
     * 流转方向
     */
    @TableField("flow_direction")
    private String flowDirection;

    /**
     * 走款账号
     */
    @TableField("payment_account")
    private String paymentAccount;
}
