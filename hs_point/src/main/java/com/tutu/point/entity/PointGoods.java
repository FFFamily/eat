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
 * 积分商品
 */
@Getter
@Setter
@TableName("point_goods")
public class PointGoods extends BaseEntity {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 商品分类ID
     */
    @TableField("type_id")
    private String typeId;
    
    /**
     * 商品名称
     */
    @TableField("goods_name")
    private String goodsName;
    
    /**
     * 商品图片
     */
    @TableField("goods_image")
    private String goodsImage;
    
    /**
     * 商品描述
     */
    @TableField("goods_description")
    private String goodsDescription;
    
    /**
     * 积分价格（需要消耗的积分）
     */
    @TableField("point_price")
    private BigDecimal pointPrice;
    
    /**
     * 库存数量
     */
    @TableField("stock")
    private Integer stock;
    
    /**
     * 状态（0-下架，1-上架）
     */
    @TableField("status")
    private String status;
    
    /**
     * 排序
     */
    @TableField("sort_num")
    private Integer sortNum;
    
    /**
     * 商品详情
     */
    @TableField("goods_detail")
    private String goodsDetail;
    
    /**
     * 商品分类名称（关联查询字段，非数据库字段）
     */
    @TableField(exist = false)
    private String typeName;
}
