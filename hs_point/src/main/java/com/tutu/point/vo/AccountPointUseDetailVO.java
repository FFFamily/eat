package com.tutu.point.vo;

import com.tutu.point.entity.AccountPointUseDetail;
import lombok.Getter;
import lombok.Setter;

/**
 * 账户积分使用详情 VO（包含用户名和商品名称）
 */
@Getter
@Setter
public class AccountPointUseDetailVO extends AccountPointUseDetail {
    
    /**
     * 用户名
     */
    private String accountName;
    
    /**
     * 商品名称
     */
    private String goodsName;
}

