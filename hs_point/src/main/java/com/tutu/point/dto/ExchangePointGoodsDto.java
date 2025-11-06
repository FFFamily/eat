package com.tutu.point.dto;

import com.tutu.point.entity.AccountPointUseDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * 兑换商品dto
 */
@Getter
@Setter
public class ExchangePointGoodsDto extends AccountPointUseDetail {
    // 兑换原因
    private String reason;
    // 备注
    private String remark;
}
