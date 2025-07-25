package com.tutu.recycle.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 回收订单
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
    // 货物照片
    private String cargoImg;
    // 运输方式
    private String transportType;
    // 经办人签字照片
    private String signImg;
}
