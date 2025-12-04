package com.tutu.point.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PointChangeTypeEnum {
    // 系统调整
    SYSTEM_ADJUST("system_adjust"),
    // 系统奖励
    SYSTEM_REWARD("system_reward"),
    // 用户使用
    USER_ADJUST("user_adjust"),
    // 活动消耗调整
    ACTIVITY_ADJUST("activity_adjust"),
    ;
    private final String type;
}
