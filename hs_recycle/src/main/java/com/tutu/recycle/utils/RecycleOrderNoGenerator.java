package com.tutu.recycle.utils;

import cn.hutool.core.util.IdUtil;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;

public class RecycleOrderNoGenerator {
    private static final String ORDER_NO_PREFIX = "RO";
    /**
     * 生成回收订单编号
     * 格式：RO + 订单类型缩写 + 时间戳 + 4位随机数
     * 例如：ROCG20240101120000123
     *
     * @param type 订单类型
     * @return 订单编号
     */
    public static String generate(RecycleOrderTypeEnum type) {
        String uuid = IdUtil.simpleUUID();
        return ORDER_NO_PREFIX + type.getAbbreviate() + System.currentTimeMillis() + uuid.substring(0, 8).toUpperCase();
    }

    public static void main(String[] args) {
        System.out.println(RecycleOrderNoGenerator.generate(RecycleOrderTypeEnum.OTHER));
    }
}
