package com.tutu.common.util;

import java.lang.reflect.Field;
import java.util.HashMap;

import com.tutu.common.annotation.FieldPropert;

public class EntityFieldUtil {
    /**
     * 获取实体类的字段标签
     * @param entityClass 实体类
     * @return 字段标签
     */
    public static HashMap<String, String> getFieldLabelMap(Class<?> entityClass) {
        HashMap<String, String> fieldLabelMap = new HashMap<>();
        Field[] fields = entityClass.getDeclaredFields();
        for (Field field : fields) {
            FieldPropert fieldPropert = field.getAnnotation(FieldPropert.class);
            if (fieldPropert != null) {
                fieldLabelMap.put(field.getName(), fieldPropert.label());
            }
        }
        return fieldLabelMap;
    }
}
