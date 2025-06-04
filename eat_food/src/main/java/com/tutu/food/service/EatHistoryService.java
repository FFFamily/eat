package com.tutu.food.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.food.Food;
import com.tutu.food.entity.history.EatHistory;
import com.tutu.food.mapper.EatHistoryMapper;
import com.tutu.food.mapper.FoodMapper;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

// 饮食历史
@Service
public class EatHistoryService extends ServiceImpl<EatHistoryMapper, EatHistory> {
    /**
     * 获取指定时间范围内当前用户的饮食历史
     * @param userId 用户ID
     * @param start 开始时间
     * @param end 结束时间
     * @return 饮食历史
     */
    public List<EatHistory> getHistoryByRangeTime(String userId,Date start, Date end){
        return list(new LambdaQueryWrapper<EatHistory>()
                .eq(EatHistory::getCreateBy, userId)
                .gt(EatHistory::getCreateTime, start)
                .lt(EatHistory::getCreateTime, end));
    }
}
