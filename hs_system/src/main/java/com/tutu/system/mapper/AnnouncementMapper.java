package com.tutu.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.system.entity.Announcement;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {
}
