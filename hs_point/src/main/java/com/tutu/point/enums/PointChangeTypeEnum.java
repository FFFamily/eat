package com.tutu.point.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointChangeTypeEnum {
    // 系统调整
    SYSTEM_ADJUST("system_adjust"),
    // 系统奖励
    SYSTEM_REWARD("system_reward")
    ;
    private final String type;
}
