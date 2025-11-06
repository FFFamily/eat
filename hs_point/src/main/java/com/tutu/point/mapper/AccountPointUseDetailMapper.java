package com.tutu.point.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tutu.point.entity.AccountPointUseDetail;
import com.tutu.point.vo.AccountPointUseDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 账户积分使用详情 Mapper
 */
@Mapper
public interface AccountPointUseDetailMapper extends BaseMapper<AccountPointUseDetail> {
    
    /**
     * 分页查询积分使用详情（带用户名和商品名称）
     * @param page 分页对象
     * @param accountId 账户ID（可选）
     * @param pointGoodsId 积分商品ID（可选）
     * @param isUsed 是否已使用（可选）
     * @return 分页结果
     */
    Page<AccountPointUseDetailVO> pageDetailWithJoin(
            Page<AccountPointUseDetailVO> page,
            @Param("accountId") String accountId,
            @Param("pointGoodsId") String pointGoodsId,
            @Param("isUsed") Boolean isUsed
    );
    
    /**
     * 根据账户ID查询积分使用详情列表（关联查询商品名称）
     * @param accountId 账户ID
     * @return 积分使用详情列表
     */
    List<AccountPointUseDetail> getByAccountIdWithJoin(@Param("accountId") String accountId);
}

