package com.tutu.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tutu.user.entity.AccountUsernameSeq;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AccountUsernameSeqMapper extends BaseMapper<AccountUsernameSeq> {

    /**
     * MySQL 用表模拟“序列”：
     * - 不存在则插入 seq=1
     * - 已存在则原子自增 seq，并把新值写入 LAST_INSERT_ID（连接级）
     */
    @Insert("INSERT INTO account_username_seq(account_type_id, seq) VALUES(#{accountTypeId}, 1) " +
            "ON DUPLICATE KEY UPDATE seq = LAST_INSERT_ID(seq + 1)")
    int upsertAndIncrement(@Param("accountTypeId") String accountTypeId);

    @Select("SELECT LAST_INSERT_ID()")
    Long selectLastInsertId();
}
