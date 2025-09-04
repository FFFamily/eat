package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.recycle.entity.RecycleFund;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecycleFundMapper extends BaseMapper<RecycleFund> {
    
    /**
     * 分页查询走款记录（带合同信息）
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    Page<RecycleFund> selectPageWithContract(Page<RecycleFund> page, @Param("query") RecycleFund query);
    
    /**
     * 统计符合条件的记录总数
     * @param query 查询条件
     * @return 记录总数
     */
    long selectCountWithContract(@Param("query") RecycleFund query);
} 