package com.tutu.food.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.food.entity.food.DietStyle;
import com.tutu.food.entity.history.EatHistory;
import org.springframework.stereotype.Service;

@Service
public interface DietStyleMapper extends BaseMapper<DietStyle> {
}
