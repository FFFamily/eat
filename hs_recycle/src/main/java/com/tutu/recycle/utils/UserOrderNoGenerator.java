package com.tutu.recycle.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;

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
     * 生成用户订单编号
     * 格式：UO + yyyyMMdd + 4位序号
     * 例如：UO202401010001
     * 
     * @return 订单编号
     */
    public static String generate() {
        // 获取当前日期，格式：yyyyMMdd
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        
        // 获取序号并自增
        int sequence = SEQUENCE.incrementAndGet();
        if (sequence > MAX_SEQUENCE) {
            SEQUENCE.set(1);
            sequence = 1;
        }
        
        // 格式化序号为4位数字，不足补0
        String sequenceStr = String.format("%04d", sequence);
        
        // 拼接订单编号
        return ORDER_NO_PREFIX + dateStr + sequenceStr;
    }
    
    /**
     * 生成用户订单编号（带随机数）
     * 格式：UO + yyyyMMdd + 6位随机数
     * 例如：UO20240101123456
     * 
     * @return 订单编号
     */
    public static String generateWithRandom() {
        // 获取当前日期，格式：yyyyMMdd
        String dateStr = DateUtil.format(new Date(), "yyyyMMdd");
        
        // 生成6位随机数字
        String randomStr = RandomUtil.randomNumbers(6);
        
        // 拼接订单编号
        return ORDER_NO_PREFIX + dateStr + randomStr;
    }
    
    /**
     * 生成用户订单编号（带时间戳）
     * 格式：UO + yyyyMMddHHmmss + 3位随机数
     * 例如：UO20240101120000123
     * 
     * @return 订单编号
     */
    public static String generateWithTimestamp() {
        // 获取当前时间戳，格式：yyyyMMddHHmmss
        String timestampStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
        
        // 生成3位随机数字
        String randomStr = RandomUtil.randomNumbers(3);
        
        // 拼接订单编号
        return ORDER_NO_PREFIX + timestampStr + randomStr;
    }
    
    /**
     * 验证订单编号格式是否正确
     * 
     * @param orderNo 订单编号
     * @return 是否正确
     */
    public static boolean validate(String orderNo) {
        if (orderNo == null || orderNo.isEmpty()) {
            return false;
        }
        
        // 检查是否以前缀开头
        if (!orderNo.startsWith(ORDER_NO_PREFIX)) {
            return false;
        }
        
        // 检查长度（UO + 8位日期 + 4位序号 = 14位）
        if (orderNo.length() < 14) {
            return false;
        }
        
        return true;
    }
    
    /**
     * 从订单编号中提取日期
     * 
     * @param orderNo 订单编号
     * @return 日期字符串（yyyyMMdd格式），如果解析失败返回null
     */
    public static String extractDate(String orderNo) {
        if (!validate(orderNo)) {
            return null;
        }
        
        try {
            // 提取日期部分（跳过前缀，取8位日期）
            return orderNo.substring(ORDER_NO_PREFIX.length(), ORDER_NO_PREFIX.length() + 8);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 重置序号计数器
     * 注意：此方法应谨慎使用，通常仅用于测试或特殊场景
     */
    public static void resetSequence() {
        SEQUENCE.set(0);
    }
}

