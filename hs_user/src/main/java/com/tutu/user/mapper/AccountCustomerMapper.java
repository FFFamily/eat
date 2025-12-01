package com.tutu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.user.entity.AccountCustomer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账户客户关系 Mapper 接口
 */
public interface AccountCustomerMapper extends BaseMapper<AccountCustomer> {

    /**
     * 根据账户ID查询客户列表（带账户名称）
     */
    List<AccountCustomer> selectByAccountIdWithAccount(@Param("accountId") String accountId);

    /**
     * 根据客户账户ID查询账户列表（带账户名称）
     */
    List<AccountCustomer> selectByCustomerAccountIdWithAccount(@Param("customerAccountId") String customerAccountId);

    /**
     * 根据ID查询详情（带账户名称）
     */
    AccountCustomer selectByIdWithAccount(@Param("id") String id);

    /**
     * 分页查询（带账户名称）
     */
    IPage<AccountCustomer> selectPageWithAccount(Page<AccountCustomer> page, @Param("query") AccountCustomer query);

    /**
     * 检查是否存在未删除的重复记录（用于校验）
     * @param accountId 账户ID
     * @param customerAccountId 客户账户ID
     * @param excludeId 排除的ID（更新时使用，可为null）
     * @return 未删除的重复记录数量
     */
    long countActiveDuplicate(@Param("accountId") String accountId, 
                              @Param("customerAccountId") String customerAccountId,
                              @Param("excludeId") String excludeId);
}

