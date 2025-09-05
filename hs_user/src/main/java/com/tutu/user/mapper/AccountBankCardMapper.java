package com.tutu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.user.entity.AccountBankCard;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户银行卡信息 Mapper 接口
 */
public interface AccountBankCardMapper extends BaseMapper<AccountBankCard> {

    /**
     * 分页查询银行卡信息（带账户名称）
     */
    Page<AccountBankCard> selectPageWithAccount(Page<AccountBankCard> page, @Param("query") AccountBankCard query);

    /**
     * 根据ID查询银行卡详情（带账户名称）
     */
    AccountBankCard selectByIdWithAccount(@Param("id") String id);

    /**
     * 根据账户ID查询银行卡列表（带账户名称）
     */
    List<AccountBankCard> selectByAccountIdWithAccount(@Param("accountId") String accountId);
}