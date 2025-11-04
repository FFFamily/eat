package com.tutu.point.enums;

import com.tutu.common.exceptions.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum PointChangeDirectionEnum {
    ADD("add"),
    SUB("sub");
    private final String value;

    /**
     * 根据变更方向获取积分值
     * @param value 变更方向
     * @param changePoint 变更积分
     * @return 积分值
     */
    public static Long getPointValue(String value,Long changePoint){
        if (ADD.value.equals(value)){
            return changePoint;
        }
        if (SUB.value.equals(value)){
            return -changePoint;
        }
        throw new RuntimeException("变更积分方向错误");
    }
}
