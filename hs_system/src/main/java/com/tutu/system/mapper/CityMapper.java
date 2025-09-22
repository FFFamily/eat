package com.tutu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.system.entity.City;
import org.apache.ibatis.annotations.Mapper;

/**
 * 城市Mapper接口
 */
@Mapper
public interface CityMapper extends BaseMapper<City> {
}
