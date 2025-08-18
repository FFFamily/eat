package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    // 订单编号
    private String no;
    // 订单类型
    private String type;
    // 状态
    private String status;
    // 起始时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    // 结束时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;
    // 订单识别码
    private String identifyCode;
    // 经办人
    private String processor;
    // 经办人电话
    private String processorPhone;
    // 订单总金额
    private BigDecimal totalAmount;
    // 订单节点时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date orderNodeTime;
    // 订单节点相关图片
    private String orderNodeImg;
    // 提货位置
    private String orderNodePickupLocation;
    // 送货位置
    private String orderNodeDeliveryLocation;
    // === 合同相关信息 ===
    // 合同编号
    private String contractNo;
    // 合同名称
    private String contractName;
    // 合作方
    private String contractPartner;
}
