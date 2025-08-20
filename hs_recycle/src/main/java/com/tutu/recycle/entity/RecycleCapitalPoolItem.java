package com.tutu.recycle.entity;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.tutu.common.entity.BaseEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 资金池项
 */
@Getter
@Setter
public class RecycleCapitalPoolItem extends BaseEntity {
    // 资金池项ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 资金池ID
    private String capitalPoolId;
    // 订单id
    private String orderId;
    // 订单金额
    private BigDecimal amount;
    // 资金方向
    private String direction;
    // 变更前余额
    private BigDecimal beforeBalance;
    // 变更后余额
    private BigDecimal afterBalance;
}
