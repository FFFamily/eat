package com.tutu.point.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
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
    // 积分明细ID
    private String detailId;
    // 积分商品ID
    private String pointGoodsId;
    // 积分商品名称
    @TableField(exist = false)
    private String pointGoodsName;
    // 兑换码
    private String exchangeCode;
    // 账户ID
    private String accountId;
    // 消耗积分
    private Long point;
    // 凭证图片
    private String voucherImage;
}
