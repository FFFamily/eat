package com.tutu.common.enums;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

/**
 * 枚举基类
 * <p>
 * <b>注意：</b>由于 Java 限制，接口的静态方法无法被子类继承。
 * 如果需要在枚举类中直接调用 {@code YourEnum.getByCode(code)}，需要在枚举类中添加以下静态方法：
 * <pre>{@code
 * public static YourEnum getByCode(String code) {
 *     return BaseEnum.getByCode(YourEnum.class, code);
 * }
 * 
 * public static YourEnum getByCodeOrThrow(String code) {
 *     return BaseEnum.getByCode(YourEnum.class, code, true);
 * }
 * }</pre>
 * <p>
 * 或者直接使用 {@code BaseEnum.getByCode(YourEnum.class, code)} 调用。
 * <p>
 * 也可以使用工具类 {@link BaseEnumHelper} 通过静态导入简化调用。
 */
public interface BaseEnum<T extends Enum<T> & BaseEnum<T, M>, M> {
    M getCode();

    /**
     * 根据code获取枚举（未找到返回null）
     * @param enumClass 枚举类
     * @param code code
     * @return 枚举，如果未找到返回null
     */
    static <T extends Enum<T> & BaseEnum<T, M>, M> T getByCode(Class<T> enumClass, M code) {
        return getByCode(enumClass, code, false);
    }

    /**
     * 根据code获取枚举
     * @param enumClass 枚举类
     * @param code code
     * @param throwIfNotFound 如果未找到是否抛出异常
     * @return 枚举，如果未找到且throwIfNotFound为false则返回null
     */
    static <T extends Enum<T> & BaseEnum<T, M>, M> T getByCode(Class<T> enumClass, M code, boolean throwIfNotFound) {
        if (code == null) {
            if (throwIfNotFound) {
                throw new IllegalArgumentException("Code cannot be null");
            }
            return null;
        }
        
        Optional<T> optional = Arrays.stream(enumClass.getEnumConstants())
                .filter(e -> Objects.equals(e.getCode(), code))
                .findFirst();
        
        if (throwIfNotFound) {
            return optional.orElseThrow(() -> new IllegalArgumentException("Invalid code: " + code));
        } else {
            return optional.orElse(null);
        }
    }
}
