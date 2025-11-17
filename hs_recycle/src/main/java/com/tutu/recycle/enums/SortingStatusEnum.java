package com.tutu.recycle.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 分拣状态枚举
 */
@Getter
@AllArgsConstructor
public enum SortingStatusEnum {

    /**
     * 待分拣
     */
    PENDING("pending", "待分拣"),

    /**
     * 分拣中（暂存）
     */
    SORTING("sorting", "分拣中"),

    /**
     * 已分拣
     */
    SORTED("sorted", "已分拣");

    /**
     * 状态代码
     */
    private final String code;

    /**
     * 状态描述
     */
    private final String description;

    /**
     * 根据代码获取枚举
     * @param code 状态代码
     * @return 对应的枚举值，如果不存在返回null
     */
    public static SortingStatusEnum getByCode(String code) {
        if (code == null) {
            return null;
        }
        for (SortingStatusEnum status : values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 验证状态代码是否有效
     * @param code 状态代码
     * @return 是否有效
     */
    public static boolean isValid(String code) {
        return getByCode(code) != null;
    }
}
