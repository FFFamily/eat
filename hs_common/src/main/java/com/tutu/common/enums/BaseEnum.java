package com.tutu.common.enums;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * 枚举基类
 */
public interface BaseEnum<T extends Enum<T> & BaseEnum<T,M>,M> {
    M getCode();

    /**
     * 根据code获取枚举
     * @param code code
     * @return 枚举
     */
    static <T extends Enum<T> & BaseEnum<T, C>, C> T getByCode(Class<T> enumClass, C code,boolean isNullThrow) {
        Optional<T> optional = Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> e.getCode().equals(code))
                .findFirst();
        if (isNullThrow){
            return optional.orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
        }else {
            return optional.orElse(null);
        }
    }


}
