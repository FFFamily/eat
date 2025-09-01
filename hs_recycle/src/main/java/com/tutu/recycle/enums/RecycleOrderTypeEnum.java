package com.tutu.recycle.enums;

import java.util.Arrays;
import java.util.List;

import com.tutu.common.enums.BaseEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecycleOrderTypeEnum implements BaseEnum<RecycleOrderTypeEnum,String> {
    // 采购订单
    PURCHASE("purchase", "采购订单"),
    // 运输
    TRANSPORT("transport", "运输"),
    // 加工
    PROCESSING("processing", "加工"),
    // 仓储
    STORAGE("storage", "仓储"),
    // 销售订单
    SALE("sale", "销售订单"),
    // 其他
    OTHER("other", "其他")
    ;
    private final String code;
    private final String title;

    // 获取资金池方向为付款的订单类型
    public static List<RecycleOrderTypeEnum> getPayOrderTypes() {
        return Arrays.asList(PURCHASE, PROCESSING, STORAGE, TRANSPORT, OTHER);
    }
    // 判断是否为资金池方向为付款的订单类型
    public static Boolean isPayOrderType(String type) {
        return getPayOrderTypes().stream().anyMatch(t -> t.getCode().equals(type));
    }
}
