package com.tutu.point.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.point.entity.AccountPointDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账户积分详情 Mapper
 */
@Mapper
public interface AccountPointDetailMapper extends BaseMapper<AccountPointDetail> {
    
    /**
     * 分页查询积分详情（带账户名称）
     * @param page 分页对象
     * @param accountId 账户ID（可选）
     * @param changeType 变更类型（可选）
     * @param changeDirection 变更方向（可选）
     * @return 分页结果
     */
    Page<AccountPointDetail> pageDetailWithJoin(
            Page<AccountPointDetail> page,
            @Param("accountId") String accountId,
            @Param("changeType") String changeType,
            @Param("changeDirection") String changeDirection
    );
    
    /**
     * 根据账户ID查询积分详情列表（带账户名称）
     * @param accountId 账户ID
     * @return 积分详情列表
     */
    List<AccountPointDetail> getByAccountIdWithJoin(@Param("accountId") String accountId);
    
    /**
     * 根据账户ID和变更类型查询积分详情列表（带账户名称）
     * @param accountId 账户ID
     * @param changeType 变更类型
     * @return 积分详情列表
     */
    List<AccountPointDetail> getByAccountIdAndChangeTypeWithJoin(
            @Param("accountId") String accountId,
            @Param("changeType") String changeType
    );
}

