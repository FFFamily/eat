package com.tutu.common.util.field_mapping;

import java.lang.reflect.Method;
import java.util.function.Function;

/**
 * 字段绑定
 */
public @interface FieldBind {
    /**
     * 绑定字段
     * 如：userId,userName
     * 那么
     * @ FieldBind(bind = "userId")
     * private Long userName;
     */
    String bind();

}
