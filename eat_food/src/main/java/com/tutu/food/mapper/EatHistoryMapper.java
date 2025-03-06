package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.history.EatHistory;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EatHistoryMapper extends BaseMapper<EatHistory> {
}
