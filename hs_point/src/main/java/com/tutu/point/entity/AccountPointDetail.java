package com.tutu.point.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 账户积分详情
 */
@Getter
@Setter
@TableName("account_point_detail")
public class AccountPointDetail extends BaseEntity {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    // 账户ID
    private String accountId;
    // 账户名称
    @TableField(exist = false)
    private String accountName;
    // 变更方向 加/减 add/sub
    private String changeDirection;
    // 变更原因
    private String changeReason;
    // 变更数量
    private Long changePoint;
    // 变更类型
    private String changeType;
    // 备注
    private String remark;
}
