package com.tutu.recycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.recycle.entity.RecycleCapitalPool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecycleCapitalPoolMapper extends BaseMapper<RecycleCapitalPool> {
    
    /**
     * 分页查询资金池（带合同和账户信息）
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    Page<RecycleCapitalPool> selectPageWithDetails(Page<RecycleCapitalPool> page, @Param("query") RecycleCapitalPool query);
    
    /**
     * 根据ID查询资金池详情（带合同和账户信息）
     * @param id 资金池ID
     * @return 资金池详情
     */
    RecycleCapitalPool selectByIdWithDetails(@Param("id") String id);
    
    /**
     * 根据合同编号查询资金池详情（带合同和账户信息）
     * @param contractNo 合同编号
     * @return 资金池详情
     */
    RecycleCapitalPool selectByContractNoWithDetails(@Param("contractNo") String contractNo);
    
    /**
     * 根据合同ID查询资金池详情（带合同和账户信息）
     * @param contractId 合同ID
     * @return 资金池详情
     */
    RecycleCapitalPool selectByContractIdWithDetails(@Param("contractId") String contractId);
    
    /**
     * 统计总数
     * @param query 查询条件
     * @return 总数
     */
    long selectCountWithDetails(@Param("query") RecycleCapitalPool query);
}

