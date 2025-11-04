package com.tutu.point.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 积分商品分类
 */
@Getter
@Setter
@TableName("point_goods_type")
public class PointGoodsType extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 分类名称
     */
    @TableField("type_name")
    private String typeName;
    
    /**
     * 分类排序
     */
    @TableField("sort_num")
    private Integer sortNum;
    
    /**
     * 状态（0-禁用，1-启用）
     */
    @TableField("status")
    private String status;
    
    /**
     * 分类描述
     */
    @TableField("description")
    private String description;
}
