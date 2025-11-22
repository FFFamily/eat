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
    PURCHASE("purchase", "采购订单","CG"),
    // 运输
    TRANSPORT("transport", "运输订单","YS"),
    // 加工
    PROCESSING("process", "加工订单","JG"),
    // 仓储
    STORAGE("storage", "仓储订单","CC"),
    // 销售订单
    SALE("sale", "销售订单","XS"),
    // 其他
    OTHER("other", "其他","QT")
    ;
    private final String code;
    private final String title;
    private final String abbreviate;

    // 获取资金池方向为付款的订单类型
    public static List<RecycleOrderTypeEnum> getPayOrderTypes() {
        return Arrays.asList(PURCHASE, PROCESSING, STORAGE, TRANSPORT, OTHER);
    }
    // 判断是否为资金池方向为付款的订单类型
    public static Boolean isPayOrderType(String type) {
        return getPayOrderTypes().stream().anyMatch(t -> t.getCode().equals(type));
    }
}
