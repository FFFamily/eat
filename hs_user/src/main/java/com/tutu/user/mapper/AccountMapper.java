package com.tutu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.user.entity.Account;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountMapper extends BaseMapper<Account> {
    /**
     * 获取当前用户类型下有多少用户数量
     */
    @Select("SELECT COUNT(*) FROM account WHERE account_type_id = #{accountTypeId}")
    Integer getUserCountByAccountTypeId(@Param("accountTypeId") String accountTypeId);
}
