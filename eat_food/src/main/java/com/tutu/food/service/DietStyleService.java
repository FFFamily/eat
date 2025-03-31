package com.tutu.food.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tutu.food.entity.food.DietStyle;
import com.tutu.food.entity.history.EatHistory;
import com.tutu.food.mapper.DietStyleMapper;
import com.tutu.food.mapper.EatHistoryMapper;
import org.springframework.stereotype.Service;

@Service
public class DietStyleService  extends ServiceImpl<DietStyleMapper, DietStyle> {
}
