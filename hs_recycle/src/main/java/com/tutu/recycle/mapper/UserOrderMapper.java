package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.recycle.entity.user.UserOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户订单 Mapper
 */
@Mapper
public interface UserOrderMapper extends BaseMapper<UserOrder> {
}

