package com.tutu.recycle.entity.order;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 回收订单轨迹实体类
 */
@Getter
@Setter
public class RecycleOrderTrace extends BaseEntity {
    /**
     * 轨迹ID
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;
    /**
     * 订单ID
     */
    private String orderId;
    /**
     * 父轨迹识别码
     */
    private String parentCode;
    /**
     * 父订单ID
     */
    private String parentOrderId;
    /**
     * 父订单编号
     */
    private String parentOrderNo;
    /**
     * 订单类型
     */
    private String parentOrderType;
    /**
     * 变更原因
     */
    private String changeReason;

}
