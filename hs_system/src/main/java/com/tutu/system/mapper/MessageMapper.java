package com.tutu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.system.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息Mapper接口
 * 使用MyBatis Plus方式，复杂查询在Service层实现
 */
@Mapper
public interface MessageMapper extends BaseMapper<Message> {

}