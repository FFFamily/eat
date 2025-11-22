package com.tutu.common.enums;

/**
 * BaseEnum 辅助工具类
 * <p>
 * 由于 Java 限制，接口的静态方法无法被子类继承。
 * 子类可以通过以下两种方式使用：
 * <p>
 * 方式1：在枚举类中添加静态方法（推荐）
 * <pre>{@code
 * public static RecycleOrderTypeEnum getByCode(String code) {
 *     return BaseEnum.getByCode(RecycleOrderTypeEnum.class, code);
 * }
 * }</pre>
 * <p>
 * 方式2：使用静态导入工具类方法
 * <pre>{@code
 * import static com.tutu.common.enums.BaseEnumHelper.getByCode;
 * 
 * RecycleOrderTypeEnum type = getByCode(RecycleOrderTypeEnum.class, "purchase");
 * }</pre>
 */
public class BaseEnumHelper {
    
    /**
     * 根据code获取枚举（未找到返回null）
     * @param enumClass 枚举类
     * @param code code
     * @return 枚举，如果未找到返回null
     */
    public static <T extends Enum<T> & BaseEnum<T, M>, M> T getByCode(Class<T> enumClass, M code) {
        return BaseEnum.getByCode(enumClass, code);
    }

    /**
     * 根据code获取枚举
     * @param enumClass 枚举类
     * @param code code
     * @param throwIfNotFound 如果未找到是否抛出异常
     * @return 枚举，如果未找到且throwIfNotFound为false则返回null
     */
    public static <T extends Enum<T> & BaseEnum<T, M>, M> T getByCode(Class<T> enumClass, M code, boolean throwIfNotFound) {
        return BaseEnum.getByCode(enumClass, code, throwIfNotFound);
    }
}

