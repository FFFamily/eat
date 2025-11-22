package com.tutu.recycle.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import com.tutu.recycle.enums.RecycleOrderTypeEnum;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用户订单编号生成器
 */
public class UserOrderNoGenerator {
    
    /**
     * 订单编号前缀
     */
    private static final String ORDER_NO_PREFIX = "UO";
    
    /**
     * 序号计数器
     */
    private static final AtomicInteger SEQUENCE = new AtomicInteger(0);
    
    /**
     * 序号最大值（到达后重置）
     */
    private static final int MAX_SEQUENCE = 9999;

    /**
     * 生成回收订单编号
     * 格式：RO + 订单类型缩写 + 时间戳 + 4位随机数
     * 例如：ROCG20240101120000123
     *
     * @param type 订单类型
     * @return 订单编号
     */
    public static String generate() {
        String uuid = IdUtil.simpleUUID();
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        int sequence = SEQUENCE.incrementAndGet();
        if (sequence > MAX_SEQUENCE) {
            SEQUENCE.set(1);
            sequence = 1;
        }
        return ORDER_NO_PREFIX + dateStr + uuid.substring(0, 8).toUpperCase() + sequence;
    }

    public static void main(String[] args) {
        System.out.println(RecycleOrderNoGenerator.generate(RecycleOrderTypeEnum.OTHER));
    }
}

