package com.tutu.user.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.user.entity.Processor;

/**
 * 经办人 Mapper 接口
 */
public interface ProcessorMapper extends BaseMapper<Processor> {
    /**
     * 分页查询经办人
     * @param page 页码
     * @param size 每页条数
     * @param processor 经办人
     * @return
     */
    List<Processor> findPage(@Param("page") Integer page, @Param("size") Integer size, @Param("processor") Processor processor);
    /**
     * 查询经办人数量
     * @param processor 经办人
     * @return
     */
    long findPageCount(@Param("processor") Processor processor);
}