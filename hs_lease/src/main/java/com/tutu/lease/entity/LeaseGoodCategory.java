package com.tutu.lease.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 租赁商品分类
 */
@Getter
@Setter
@TableName("lease_good_category")
public class LeaseGoodCategory extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 分类编码
     */
    private String code;
    /**
     * 分类名称
     */
    private String name;
}
