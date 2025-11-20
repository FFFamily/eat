package com.tutu.recycle.request;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.tutu.recycle.entity.user.UserOrder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
@Getter
@Setter
public class WxUserCreateOrderRequest extends UserOrder {
    // 经办人id
    private String processorId;
    // 订单图片
    private String orderNodeImg;
    // 合同id
    private String contractId;
    // 拿货地址
    private String pickupAddress;
    // 交付地址
    private String deliveryAddress;
    // 运输方式（送货上门/自提等）
    private String transportMethod;
}
