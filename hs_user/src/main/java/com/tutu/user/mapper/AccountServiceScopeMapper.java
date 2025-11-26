package com.tutu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.user.entity.AccountServiceScope;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账户服务范围 Mapper 接口
 */
public interface AccountServiceScopeMapper extends BaseMapper<AccountServiceScope> {

    /**
     * 根据账户ID查询服务范围列表（带账户名称）
     */
    List<AccountServiceScope> selectByAccountIdWithAccount(@Param("accountId") String accountId);
}

