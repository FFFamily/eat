package com.tutu.system.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息来源枚举类
 */
@Getter
@AllArgsConstructor
public enum MessageOriginEnum {
    /**
     * 系统消息
     */
    SYSTEM("system"),
    /**
     * 用户消息
     */
    USER("user");
    private final String code;
}
