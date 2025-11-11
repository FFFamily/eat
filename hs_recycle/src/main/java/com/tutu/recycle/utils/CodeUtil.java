package com.tutu.recycle.utils;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tutu.recycle.entity.order.RecycleOrder;
import com.tutu.recycle.entity.user.UserOrder;
import com.tutu.recycle.mapper.RecycleOrderMapper;
import com.tutu.recycle.mapper.UserOrderMapper;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * 订单识别码生成工具类
 */
@Component
public class CodeUtil {
    
    @Resource
    private RecycleOrderMapper recycleOrderMapper;
    
    /**
     * 生成订单识别码
     * 格式：前4位是随机的大写字母和数字组成，后2位是当天订单的流水号
     * 例如：A3B901、X9Y202
     * 
     * @return 订单识别码（6位）
     */
    public String generateOrderCode() {
        // 生成前4位随机大写字母和数字
        String randomPart = generateRandomAlphanumeric(4);
        
        // 获取当天订单的流水号（从1开始）
        int sequence = getTodayOrderSequence();
        
        // 格式化流水号为2位数字，不足补0
        String sequenceStr = String.format("%02d", sequence);
        
        // 拼接订单识别码
        return randomPart + sequenceStr;
    }
    
    /**
     * 生成指定长度的随机大写字母和数字字符串
     * 
     * @param length 长度
     * @return 随机字符串
     */
    private String generateRandomAlphanumeric(int length) {
        // 大写字母和数字的字符集
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            int index = RandomUtil.randomInt(chars.length());
            sb.append(chars.charAt(index));
        }
        
        return sb.toString();
    }
    
    /**
     * 获取当天订单的流水号
     * 查询当天创建的订单数量，返回下一个流水号
     * 
     * @return 当天订单流水号（从1开始）
     */
    private int getTodayOrderSequence() {
        // 获取当天的开始时间和结束时间
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        
        // 查询当天创建的订单数量
        LambdaQueryWrapper<RecycleOrder> wrapper = new LambdaQueryWrapper<>();
        wrapper.ge(RecycleOrder::getCreateTime, startOfDay)
               .le(RecycleOrder::getCreateTime, endOfDay);
        
        long count = recycleOrderMapper.selectCount(wrapper);
        
        // 流水号从1开始，所以返回 count + 1
        // 如果当天订单数超过99，则从01重新开始（使用取模运算）
        // 例如：第1个订单=01, 第99个订单=99, 第100个订单=01
        return (int) ((count % 99) + 1);
    }
}