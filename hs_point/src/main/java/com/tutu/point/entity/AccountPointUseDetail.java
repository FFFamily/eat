package com.tutu.point.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 账户积分使用详情
 */
@Getter
@Setter
@TableName("account_point_use_detail")
public class AccountPointUseDetail extends BaseEntity {
    // 主键ID
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 是否已使用
    private Boolean isUsed;
    // 积分商品ID
    private String pointGoodsId;
    // 兑换码
    private String exchangeCode;
    // 账户ID
    private String accountId;
    // 消耗积分
    private Long point;
}
