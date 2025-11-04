package com.tutu.point.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 积分全局配置
 */
@Getter
@Setter
@TableName("point_global_config")
public class PointGlobalConfig extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 积分比例
     */
    @TableField("point_ratio")
    private BigDecimal pointRatio;
}
