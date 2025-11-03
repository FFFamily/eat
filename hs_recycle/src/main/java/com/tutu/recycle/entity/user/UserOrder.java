package com.tutu.recycle.entity.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserOrder extends BaseEntity {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    // 订单编号
    private String no;
    //订单状态 阶段
    private String stage;
    // 订单状态
    private String status;
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
    // 订单图片
    private String imgUrl;
    // 位置
    private String location;
    // 经办人id
    private String processorId;
    // 经办人名称
    @TableField(exist = false)
    private String processorName;
}
