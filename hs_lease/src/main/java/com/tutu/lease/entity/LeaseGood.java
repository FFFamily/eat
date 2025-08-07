package com.tutu.lease.entity;

import com.tutu.common.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

// 租赁商品
@Getter
@Setter
public class LeaseGood extends BaseEntity {
    // id
    private String id;
    // 编码
    private String code;
    // 商品名称
    private String name;
    // 商品图片
    private String image;
    // 商品分类
    private String type;
    // 状态(上架、下架)
    private String status;
    // 基本信息
    private String baseInfo;
    // 使用年限
    private BigDecimal useLimitYear;
    // 设备参数
    private String parameter;
    // 服务内容
    private String serviceContent;
    // 注意事项
    private String precaution;
    // 价格(元/天)
    private BigDecimal price;
}
