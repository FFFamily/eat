package com.tutu.admin_user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.admin_user.entity.AdUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 管理员用户Mapper接口
 * 使用MyBatis Plus方式，复杂查询在Service层实现
 */
@Mapper
public interface AdUserMapper extends BaseMapper<AdUser> {

}
